package com.filipowm.ambassador.project.indexer.internals

import com.filipowm.ambassador.configuration.properties.IndexerProperties
import com.filipowm.ambassador.configuration.properties.IndexingLockType
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.extensions.toPrettyString
import com.filipowm.ambassador.project.indexer.IndexingLock
import com.filipowm.ambassador.storage.indexing.IndexingRepository
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
        return when(indexerProperties.lockType) {
            IndexingLockType.IN_MEMORY -> IndexingLock.createInMemoryLock()
            IndexingLockType.DATABASE -> IndexingLock.createDatabaseLock(indexingRepository)
        }
    }

}