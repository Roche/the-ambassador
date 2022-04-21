package com.roche.ambassador.projects.cleanup

import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.extensions.isBefore
import com.roche.ambassador.storage.indexing.IndexingRepository
import com.roche.ambassador.storage.indexing.IndexingStatus
import com.roche.ambassador.storage.project.ProjectEntityRepository
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
@ConditionalOnProperty(prefix = "ambassador.indexer.cleanup", name = ["enabled"], havingValue = "true", matchIfMissing = true)
internal open class ObsoleteProjectsCleaner(
    private val indexerProperties: IndexerProperties,
    private val projectEntityRepository: ProjectEntityRepository,
    private val indexingRepository: IndexingRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    companion object {
        private val log by LoggerDelegate()
    }

    @Scheduled(cron = "\${ambassador.indexer.cleanup.cron}")
    @SchedulerLock(name = "old-project-cleanup", lockAtMostFor = "5m", lockAtLeastFor = "3m")
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    open fun cleanup() {
        // TODO unfix target/source for multi-source support
        val cleanupObsoleteProjectsBeforeDate = LocalDateTime.now().minus(indexerProperties.cleanup.cleanupOlderThan)
        val lastFinishedIndexingDate = indexingRepository.findFirstByTargetAndStatusOrderByStartedDateDesc("gitlab", IndexingStatus.FINISHED)
            .map { it.startedDate }
        if (lastFinishedIndexingDate.isEmpty) {
            log.warn("It is not possible to cleanup projects before any fully finished indexing")
            return
        }
        // if last finished indexing is before date to cleanup projects, then use last finished indexing date
        val cleanupDateToUse = if (lastFinishedIndexingDate.isBefore(cleanupObsoleteProjectsBeforeDate)) {
            lastFinishedIndexingDate.get()
        } else {
            cleanupObsoleteProjectsBeforeDate
        }
        log.info("Cleaning up projects which were indexed last time before {}", cleanupDateToUse)
        val cleanedUpCount = projectEntityRepository.deleteAllBySourceAndLastIndexedDateIsBefore("gitlab", cleanupDateToUse)
        log.info("Cleaned up {} projects", cleanedUpCount)
        applicationEventPublisher.publishEvent(ObsoleteProjectsCleanedEvent())
    }
}
