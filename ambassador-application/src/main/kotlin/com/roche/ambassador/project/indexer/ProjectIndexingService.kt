package com.roche.ambassador.project.indexer

import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.configuration.properties.IndexingCriteriaProperties
import com.roche.ambassador.configuration.source.ProjectSources
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.project.ProjectFilter
import com.roche.ambassador.security.AuthenticationContext
import com.roche.ambassador.storage.indexing.Indexing
import com.roche.ambassador.storage.indexing.IndexingRepository
import com.roche.ambassador.storage.indexing.IndexingStatus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
internal class ProjectIndexingService(
    private val sources: ProjectSources,
    private val indexerFactory: IndexerFactory,
    private val indexingRepository: IndexingRepository,
    private val indexingLock: IndexingLock,
    private val eventPublisher: ApplicationEventPublisher,
    indexerProperties: IndexerProperties
) {

    private val criteriaProperties: IndexingCriteriaProperties = indexerProperties.criteria
    private val indexersInUse: MutableMap<UUID, ProjectIndexer> = ConcurrentHashMap()

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
            project.name,
            project.id,
            failedCriteriaString
        )
        stats.recordExclusion(failedCriteria)
    }

    @Suppress("UNCHECKED_CAST")
    private fun createIndexer(sourceName: String): ProjectIndexer {
        // FIXME don't get fixed source
        val source = sources.get(sourceName).get()
        return indexerFactory.create(source)
    }

    // FIXME don't hardcode source name!
    suspend fun reindex(sourceName: String = "gitlab"): IndexingDto {
        val user = AuthenticationContext.currentUserNameOrElse("unknown")
        log.info("Indexing of all projects within source repository started by {}", user)
        var idx = Indexing.startAll(startedBy = user, source = sourceName)
        if (indexingLock.tryLock(idx)) {
            val indexer = createIndexer(sourceName)
            idx = indexingRepository.save(idx)
            indexersInUse[idx.getId()!!] = indexer
            val filter = ProjectFilter.Builder()
                .archived(!criteriaProperties.projects.excludeArchived)
                .groups(*criteriaProperties.projects.groups.toTypedArray())
                .visibility(criteriaProperties.projects.maxVisibility)
                .lastActivityAfter(criteriaProperties.projects.lastActivityAfter)
                .build()
            val stats = Statistics()
            indexer.indexAll(
                filter = filter,
                onStarted = { stats.startTiming() },
                onProjectIndexingStarted = { stats.recordStarted() },
                onProjectIndexingFinished = { stats.recordFinished() },
                onProjectIndexingError = { t, _ -> stats.recordError(t) },
                onProjectExcludedByCriteria = { criteria, project -> handleExcludedProject(stats, criteria, project) },
                onFinished = {
                    stats.stopTiming()
                    idx.finish(stats.asIndexingStatistics())
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

    // FIXME don't hardcode source name!
    suspend fun reindex(id: Long, sourceName: String = "gitlab"): Project? {
        val indexer = createIndexer(sourceName)
        return indexer.indexOne(id)
    }
}
