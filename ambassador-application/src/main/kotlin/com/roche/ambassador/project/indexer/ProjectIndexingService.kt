package com.roche.ambassador.project.indexer

import com.roche.ambassador.configuration.source.ProjectSources
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.source.ProjectDetailsResolver
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.security.AuthenticationContext
import com.roche.ambassador.storage.indexing.Indexing
import com.roche.ambassador.storage.indexing.IndexingRepository
import com.roche.ambassador.storage.indexing.IndexingStatus
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
internal class ProjectIndexingService(
    private val sources: ProjectSources,
    private val indexerFactory: IndexerFactory,
    private val indexingRepository: IndexingRepository,
    private val indexingLock: IndexingLock
) {

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
        projectDetailsResolver: ProjectDetailsResolver<Any>,
        stats: Statistics,
        failedCriteria: List<IndexingCriterion<Any>>,
        project: Any
    ) {
        val failedCriteriaString = failedCriteria.joinToString(", ") { it.name }
        log.warn(
            "Project '{}' (id={}) is excluded from indexing because not meet criteria: {}",
            projectDetailsResolver.resolveName(project),
            projectDetailsResolver.resolveId(project),
            failedCriteriaString
        )
        stats.recordExclusion(failedCriteria)
    }

    @Suppress("UNCHECKED_CAST")
    private fun createIndexer(): ProjectIndexer {
        val source = sources.get("gitlab").get() as ProjectSource<Any>
        return indexerFactory.create(source)
    }

    suspend fun reindex(): IndexingDto {
        val user = AuthenticationContext.currentUserNameOrElse("unknown")
        log.info("Indexing of all projects within source repository started by {}", user)
        var idx = Indexing.startAll(startedBy = user)
        if (indexingLock.tryLock(idx)) {
            val indexer = createIndexer()
            idx = indexingRepository.save(idx)
            indexersInUse[idx.getId()!!] = indexer
            val stats = Statistics()
            indexer.indexAll(
                onStarted = { stats.startTiming() },
                onProjectIndexingStarted = { stats.recordStarted() },
                onProjectIndexingFinished = { stats.recordFinished() },
                onProjectIndexingError = { t, _ -> stats.recordError(t) },
                onProjectExcludedByCriteria = { criteria, project -> handleExcludedProject(indexer.getSource(), stats, criteria, project) },
                onFinished = {
                    stats.stopTiming()
                    idx.finish(stats.asIndexingStatistics())
                    indexingRepository.save(idx)
                    indexersInUse.remove(idx.getId())
                    indexingLock.unlock(idx.getId()!!)
                    log.info("Indexing has finished")
                    log.info("Report:\n{}", stats.getReport())
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

    suspend fun reindex(id: Long): Project? {
        val indexer = createIndexer()
        return indexer.indexOne(id)
    }
}
