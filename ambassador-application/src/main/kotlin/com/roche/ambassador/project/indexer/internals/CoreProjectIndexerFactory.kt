package com.roche.ambassador.project.indexer.internals

import com.roche.ambassador.ConcurrencyProvider
import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.project.indexer.IndexerFactory
import com.roche.ambassador.project.indexer.ProjectIndexer
import com.roche.ambassador.storage.project.ProjectEntityRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@Component
internal class CoreProjectIndexerFactory(
    private val projectEntityRepository: ProjectEntityRepository,
    private val concurrencyProvider: ConcurrencyProvider,
    private val indexerProperties: IndexerProperties,
    private val platformTransactionManager: PlatformTransactionManager
) : IndexerFactory {
    override fun create(source: ProjectSource<Any>, ): ProjectIndexer {
        val criteria = IndexingCriteria.forProvider(source, source)
        return CoreProjectIndexer(
            source,
            projectEntityRepository,
            concurrencyProvider,
            indexerProperties,
            criteria,
            TransactionTemplate(platformTransactionManager)
        )
    }
}