package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.configuration.source.ProjectSources
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.model.project.Project
import com.filipowm.ambassador.model.source.ProjectDetailsResolver
import com.filipowm.ambassador.model.source.ProjectSource
import com.filipowm.ambassador.security.AuthenticationContext
import com.filipowm.ambassador.storage.indexing.Indexing
import com.filipowm.ambassador.storage.indexing.IndexingRepository
import org.springframework.stereotype.Component

@Component
internal class ProjectIndexingService(
    private val sources: ProjectSources,
    private val indexerFactory: IndexerFactory,
    private val indexingRepository: IndexingRepository,
    private val indexingLock: IndexingLock
) {

    @Volatile
    private var currentIndexerUsed: ProjectIndexer? = null

    companion object {
        private val log by LoggerDelegate()
    }

    suspend fun forciblyStop(terminateImmediately: Boolean) {
        log.info("Trying to forcibly stop indexing, if active")
        if (indexingLock.isLocked() && currentIndexerUsed != null) {
            (currentIndexerUsed ?: return).forciblyStop(terminateImmediately)
            log.warn("Indexing forcibly stopped!")
        } else {
            log.warn("No indexing in progress, nothing to stop")
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
            currentIndexerUsed = indexer
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
                    currentIndexerUsed = null
                    indexingLock.unlock(idx)
                    log.warn("Indexing has finished")
                    log.warn("Report:\n{}", stats.getReport())
                }
            )
            return IndexingDto.from(idx)
        } else {
            val indexing = indexingRepository.findByLockIsNotNullAndTarget(idx.target).orElse(idx)
            log.warn("Unable to trigger new indexing, cause indexing '{}' is already in progress and locked.", indexing.getId())
            throw IndexingAlreadyStartedException("Unable to start new projects indexing, because it is already running", indexing)
        }
    }

    suspend fun reindex(id: Long): Project? {
        val indexer = createIndexer()
        return indexer.indexOne(id)
    }
}
