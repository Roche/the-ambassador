package com.roche.ambassador.project.indexer.internals

import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.configuration.properties.IndexingLockType
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.extensions.toPrettyString
import com.roche.ambassador.project.indexer.IndexingLock
import com.roche.ambassador.storage.indexing.IndexingRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class IndexingConfiguration(
    private val indexerProperties: IndexerProperties
) {

    private val log by LoggerDelegate()

    @Bean
    fun indexingLock(indexingRepository: IndexingRepository): IndexingLock {
        log.info("Indexer will use {} locking mechanism", indexerProperties.lockType.toPrettyString())
        return when (indexerProperties.lockType) {
            IndexingLockType.IN_MEMORY -> IndexingLock.createInMemoryLock()
            IndexingLockType.DATABASE -> IndexingLock.createDatabaseLock(indexingRepository)
        }
    }
}
