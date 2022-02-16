package com.roche.ambassador.indexing

import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.configuration.properties.IndexingCriteriaProperties
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.project.ProjectIndexer
import com.roche.ambassador.indexing.project.Statistics
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.project.ProjectFilter
import com.roche.ambassador.model.source.ProjectSources
import com.roche.ambassador.security.AuthenticationContext
import com.roche.ambassador.storage.indexing.Indexing
import com.roche.ambassador.storage.indexing.IndexingRepository
import com.roche.ambassador.storage.indexing.IndexingStatus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
internal class IndexingService(
    private val sources: ProjectSources,
    private val indexerFactory: IndexerFactory,
    private val indexingRepository: IndexingRepository,
    private val indexingLock: IndexingLock,
    private val eventPublisher: ApplicationEventPublisher,
    indexerProperties: IndexerProperties
) {

    private val criteriaProperties: IndexingCriteriaProperties = indexerProperties.criteria
    private val indexersInUse: MutableMap<UUID, Indexer<*, *, *>> = ConcurrentHashMap()

    companion object {
        private val log by LoggerDelegate()
    }

    suspend fun forciblyStop(indexingId: UUID, terminateImmediately: Boolean) {
        log.info("Trying to forcibly stop indexing '{}', if active", indexingId)
        when {
            indexersInUse.containsKey(indexingId) -> {
                indexersInUse[indexingId]?.forciblyStop(terminateImmediately)
                indexersInUse.remove(indexingId)
                indexingLock.unlock(indexingId)
                log.warn("Indexing '{}' forcibly stopped!", indexingId)
            }
            indexingLock.isLocked(indexingId) -> indexingLock.unlock(indexingId)
            else -> log.warn("No indexing in progress, nothing to stop")
        }
    }

    suspend fun forciblyStopAll(terminateImmediately: Boolean) {
        log.info("Trying to forcibly stop all active indexers")
        val entriesIterator = indexersInUse.entries.iterator()
        while (entriesIterator.hasNext()) {
            val entry = entriesIterator.next()
            log.warn("About to stop indexing '{}'", entry.key)
            entry.value.forciblyStop(terminateImmediately)
            indexingLock.unlock(entry.key)
            log.warn("Indexing '{}' stopped forcibly", entry.key)
            entriesIterator.remove()
        }
    }

    private fun handleExcludedProject(
        stats: Statistics,
        failedCriteria: List<IndexingCriterion>,
        project: Project
    ) {
        val failedCriteriaString = failedCriteria.joinToString(", ") { it.name }
        log.warn(
            "Project '{}' (id={}) is excluded from indexing because not meet criteria: {}",
            project.fullName,
            project.id,
            failedCriteriaString
        )
        stats.recordExclusion(failedCriteria)
    }

    @Suppress("UNCHECKED_CAST")
    private fun createIndexer(sourceName: String, indexing: Indexing, continuation: Continuation): ProjectIndexer {
        // FIXME don't get fixed source
        val source = sources.get(sourceName).get()
        return indexerFactory.create(source, indexing, continuation)
    }

    private suspend fun resolveUser() = AuthenticationContext.currentUserNameOrElse("unknown")

    private fun resolveContinuationPoint(sourceName: String): Continuation {
        val history = indexingRepository.findLastFinishedAndAllFollowingWithinLastDayForSource(sourceName)
        return Continuation(history, criteriaProperties)
    }

    // FIXME don't hardcode source name!
    suspend fun reindex(sourceName: String = "gitlab"): IndexingDto {
        val user = resolveUser()
        log.info("Indexing of all projects within source repository {} started by {}", sourceName, user)
        var idx = Indexing.startAll(startedBy = user, source = sourceName)
        if (indexingLock.tryLock(idx)) {
            val continuation = resolveContinuationPoint(sourceName)
            logContinuationPoint(continuation)
            idx = indexingRepository.save(idx)
            val filter = ProjectFilter.Builder()
                .archived(!criteriaProperties.projects.excludeArchived)
                .groups(*criteriaProperties.projects.groups.toTypedArray())
                .visibility(criteriaProperties.projects.maxVisibility)
                .lastActivityAfter(continuation.lastActivityAfter)
                .build()
            val stats = Statistics()
            val indexer = createIndexer(sourceName, idx, continuation)
            indexersInUse[idx.getId()!!] = indexer
            indexer.indexAll(
                filter = filter,
                onStarted = { stats.startTiming() },
                onObjectIndexingStarted = { stats.recordStarted() },
                onObjectIndexingFinished = { stats.recordFinished() },
                onObjectIndexingError = { t, _ -> stats.recordError(t) },
                onObjectExcludedByCriteria = { criteria, project -> handleExcludedProject(stats, criteria, project) },
                onFinished = {
                    stats.stopTiming()
                    idx.finish(indexer.getStatus(), stats.asIndexingStatistics())
                    indexingRepository.save(idx)
                    indexersInUse.remove(idx.getId())
                    indexingLock.unlock(idx.getId()!!)
                    log.info("Projects indexing has finished")
                    log.info("Report:\n{}", stats.getReport())
                    eventPublisher.publishEvent(IndexingFinishedEvent(idx))
                }
            )
            return IndexingDto.from(idx)
        } else {
            val indexing = indexingRepository.findByLockIsNotNullAndTarget(idx.target)
                .or { indexingRepository.findFirstByTargetAndStatusOrderByStartedDateDesc(idx.target, IndexingStatus.IN_PROGRESS) }
                .orElse(idx)

            log.warn("Unable to trigger new indexing, cause indexing '{}' is already in progress and locked.", indexing.getId())
            throw IndexingAlreadyStartedException("Unable to start new projects indexing, because it is already running", indexing)
        }
    }

    private fun logContinuationPoint(continuation: Continuation) {
        when {
            continuation.full -> log.info("Full indexing will be executed for all project after {}", continuation.lastActivityAfter)
            continuation.resumed -> log.info("Indexing will be resumed with continuation: {}", continuation)
            continuation.incrementalOnly -> log.info("Incremental indexing will be executed for all projects after {}", continuation.lastActivityAfter)
            else -> log.warn("Indexing will run with unknown continuation status: {}", continuation)
        }
    }

    // FIXME don't hardcode source name!
    suspend fun reindex(id: Long, sourceName: String = "gitlab"): Project {
        val user = resolveUser()
        log.info("Indexing of {} project within source repository {} started by {}", id, sourceName, user)
        var idx = Indexing.start(startedBy = user, source = sourceName, target = id.toString())
        idx = indexingRepository.save(idx)
        val indexer = createIndexer(sourceName, idx, Continuation.none())
        try {
            val result = indexer.indexOne(id)
            indexingRepository.save(idx.finish(indexer.getStatus()))
            eventPublisher.publishEvent(SingleProjectIndexingFinishedEvent(result, idx))
            return result
        } catch(e: Throwable) {
            indexingRepository.save(idx.fail())
            throw e
        }
    }
}
