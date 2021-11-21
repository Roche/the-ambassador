package com.roche.ambassador.project.indexer.internals

import com.roche.ambassador.ConcurrencyProvider
import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.model.source.IndexingCriteriaProvider
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.project.indexer.IndexerFactory
import com.roche.ambassador.project.indexer.ProjectIndexer
import com.roche.ambassador.project.indexer.steps.*
import com.roche.ambassador.storage.project.ProjectEntityRepository
import org.springframework.stereotype.Component

@Component
internal class CoreProjectIndexerFactory(
    private val projectEntityRepository: ProjectEntityRepository,
    private val concurrencyProvider: ConcurrencyProvider,
    private val indexerProperties: IndexerProperties,
    private val steps: List<IndexingStep>
) : IndexerFactory {
    override fun create(source: ProjectSource): ProjectIndexer {
        val criteria = IndexingCriteria.forProvider(IndexingCriteriaProvider, indexerProperties.criteria)
        return CoreProjectIndexer(
            source,
            projectEntityRepository,
            concurrencyProvider,
            indexerProperties,
            criteria,
            steps
        )
    }
}
