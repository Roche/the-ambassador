package com.roche.ambassador.indexing

import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.configuration.properties.IndexingLockType
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.extensions.toPrettyString
import com.roche.ambassador.storage.indexing.IndexingRepository
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import javax.sql.DataSource

@Configuration
internal class IndexingConfiguration(private val indexerProperties: IndexerProperties) {

    companion object {
        private val log by LoggerDelegate()
    }

    @Bean
    fun indexingLock(indexingRepository: IndexingRepository): IndexingLock {
        log.info("Indexer will use {} locking mechanism", indexerProperties.lockType.toPrettyString())
        return when (indexerProperties.lockType) {
            IndexingLockType.IN_MEMORY -> IndexingLock.createInMemoryLock()
            IndexingLockType.DATABASE -> IndexingLock.createDatabaseLock(indexingRepository)
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "ambassador.indexer.scheduler", name = ["enabled"], matchIfMissing = false, havingValue = "true")
    @EnableScheduling
    @EnableSchedulerLock(defaultLockAtMostFor = "10m")
    inner class SchedulerConfiguration : InitializingBean {

        override fun afterPropertiesSet() {
            log.info("Initialized indexing scheduler with cron: ${indexerProperties.scheduler.cron}")
        }

        @Bean
        fun lockProvider(dataSource: DataSource): LockProvider {
            return JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                    .withJdbcTemplate(JdbcTemplate(dataSource))
                    .usingDbTime()
                    .build()
            )
        }

        @Bean
        fun indexingScheduler(indexingService: IndexingService): ScheduledIndexingInitializer {
           return ScheduledIndexingInitializer(indexingService)
        }
    }

}
