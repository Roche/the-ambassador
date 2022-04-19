package com.roche.ambassador.indexing

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.security.RunAsTechnicalUser
import kotlinx.coroutines.runBlocking
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled

internal open class ScheduledIndexingInitializer(private val indexingService: IndexingService) {

    companion object {
        private val log by LoggerDelegate()
    }

    @Scheduled(cron = "\${ambassador.indexer.scheduler.cron}")
    @SchedulerLock(name = " indexing", lockAtLeastFor = "\${ambassador.indexer.scheduler.lockFor}", lockAtMostFor = "1h")
    @RunAsTechnicalUser
    open fun triggerScheduling() {
        log.info("Triggering scheduled indexing")
        // purposely run blocking, cause it will get async in downstream when indexing is triggered successfully
        runBlocking {
            try {
                indexingService.reindex()
            } catch (ex: IndexingAlreadyStartedException) {
                log.warn("Unable to start scheduled indexing because it is already running")
            }
        }
    }
}