package com.roche.ambassador.indexing.project

import com.roche.ambassador.ConcurrencyProvider
import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.indexing.IndexerFactory
import com.roche.ambassador.indexing.project.steps.IndexingStep
import com.roche.ambassador.model.source.IndexingCriteriaProvider
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.storage.project.ProjectEntityRepository
import org.springframework.stereotype.Component

@Component
internal class ProjectIndexerFactory(
    private val projectEntityRepository: ProjectEntityRepository,
    private val concurrencyProvider: ConcurrencyProvider,
    private val indexerProperties: IndexerProperties,
    private val steps: List<IndexingStep>
) : IndexerFactory {
    override fun create(source: ProjectSource): ProjectIndexer {
        val criteria = IndexingCriteria.forProvider(IndexingCriteriaProvider, indexerProperties.criteria)
        return ProjectIndexer(
            source,
            projectEntityRepository,
            concurrencyProvider,
            indexerProperties,
            criteria,
            steps
        )
    }
}
