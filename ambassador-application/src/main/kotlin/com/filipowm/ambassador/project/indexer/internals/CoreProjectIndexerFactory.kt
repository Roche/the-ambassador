package com.filipowm.ambassador.project.indexer.internals

import com.filipowm.ambassador.ConcurrencyProvider
import com.filipowm.ambassador.configuration.source.ProjectSourcesProperties
import com.filipowm.ambassador.model.source.ProjectSource
import com.filipowm.ambassador.project.indexer.IndexerFactory
import com.filipowm.ambassador.project.indexer.ProjectIndexer
import com.filipowm.ambassador.storage.project.ProjectEntityRepository
import org.springframework.stereotype.Component

@Component
internal class CoreProjectIndexerFactory(
    private val projectEntityRepository: ProjectEntityRepository,
    private val concurrencyProvider: ConcurrencyProvider,
    private val projectSourcesProperties: ProjectSourcesProperties
) : IndexerFactory {
    override fun create(source: ProjectSource<Any>, ): ProjectIndexer {
        val criteria = IndexingCriteria.forProvider(source, source)
        return CoreProjectIndexer(
            source,
            projectEntityRepository,
            concurrencyProvider,
            projectSourcesProperties.indexEvery,
            criteria
        )
    }
}