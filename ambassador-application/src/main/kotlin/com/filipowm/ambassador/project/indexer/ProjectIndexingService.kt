package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.ConcurrencyProvider
import com.filipowm.ambassador.configuration.source.ProjectSources
import com.filipowm.ambassador.configuration.source.ProjectSourcesProperties
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.model.Project
import com.filipowm.ambassador.model.criteria.IndexingCriteria
import com.filipowm.ambassador.model.source.ProjectSource
import com.filipowm.ambassador.storage.ProjectEntityRepository
import org.springframework.stereotype.Component

@Component
internal class ProjectIndexingService(
    private val sources: ProjectSources,
    private val projectEntityRepository: ProjectEntityRepository,
    private val concurrencyProvider: ConcurrencyProvider,
    private val projectSourceProperties: ProjectSourcesProperties,

    ) {
    private val indexingLock: IndexingLock = InMemoryIndexinglock()

    @Volatile
    private var currentIndexerUsed: ProjectIndexer? = null

    companion object {
        private val log by LoggerDelegate()
    }

    suspend fun forciblyStop() {
        if (indexingLock.isLocked() && currentIndexerUsed != null) {
            currentIndexerUsed!!.forciblyStop()
        }
    }

    suspend fun reindex() {
        log.info("Starting indexing all projects within source repository")
        if (indexingLock.tryLock()) {
            val indexer = createIndexer()
            currentIndexerUsed = indexer
            return indexer.indexAll(
                onFinished = {
                    currentIndexerUsed = null
                    indexingLock.unlock()
                    log.warn("Indexing has finished")
                }
            )
        } else {
            log.warn("Unable to trigger new indexing, cause indexing is already in progress and locked.")
            throw IndexingAlreadyStartedException("Unable to start new projects indexing, because it is already running")
        }
    }

    suspend fun reindex(id: Long): Project? {
        val indexer = createIndexer()
        return indexer.indexOne(id)
    }

    private fun createIndexer(): ProjectIndexer {
        val source = sources.get("gitlab").get() as ProjectSource<Any>
        val indexer = CoreProjectIndexer(
            source,
            projectEntityRepository,
            concurrencyProvider,
            projectSourceProperties.indexEvery,
            IndexingCriteria(source.getInvalidProjectCriterions().hasRepositorySetUp())
        )
        log.info("Using '{}' for indexing projects", indexer.javaClass.canonicalName)
        return indexer
    }
}