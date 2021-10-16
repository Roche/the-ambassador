package com.filipowm.ambassador.project.indexer.internals

import com.filipowm.ambassador.ConcurrencyProvider
import com.filipowm.ambassador.configuration.properties.IndexerProperties
import com.filipowm.ambassador.model.source.ProjectSource
import com.filipowm.ambassador.project.indexer.IndexerFactory
import com.filipowm.ambassador.project.indexer.ProjectIndexer
import com.filipowm.ambassador.storage.project.ProjectEntityRepository
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