package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.ConcurrencyProvider
import com.filipowm.ambassador.configuration.source.ProjectSources
import com.filipowm.ambassador.configuration.source.ProjectSourcesProperties
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.gitlab.GitLabSource
import com.filipowm.ambassador.model.Project
import com.filipowm.ambassador.model.source.ProjectSource
import com.filipowm.ambassador.storage.ProjectEntityRepository
import org.springframework.stereotype.Component

@Component
internal class ProjectIndexingService(
    private val sources: ProjectSources,
    private val projectEntityRepository: ProjectEntityRepository,
    private val concurrencyProvider: ConcurrencyProvider,
    private val projectSourceProperties: ProjectSourcesProperties
) {
    private val useNewIndexer: Boolean = false

    companion object {
        private val log by LoggerDelegate()
    }

    suspend fun reindex() {
        val indexer = createIndexer()
        return indexer.indexAll()
    }

    suspend fun reindex(id: Long): Project? {
        val indexer = createIndexer()
        return indexer.indexOne(id)
    }

    private fun createIndexer(): ProjectIndexer {
        val indexer = if (useNewIndexer) {
            CoreProjectIndexer(sources.get("gitlab").get() as ProjectSource<Any>, projectEntityRepository, concurrencyProvider, projectSourceProperties.indexEvery)
        } else {
            LegacyProjectIndexer(sources.get("gitlab").get() as GitLabSource, projectEntityRepository, concurrencyProvider, projectSourceProperties)
        }
        log.info("Using '{}' for indexing projects", indexer.javaClass.canonicalName)
        return indexer
    }
}