package com.roche.ambassador.lookups

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.IndexingFinishedEvent
import com.roche.ambassador.projects.cleanup.ObsoleteProjectsCleanedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

@Component
internal class LookupSynchronizer(private val lookupServices: List<LookupService<*, *>>) {

    private val lock: Lock = ReentrantLock()

    companion object {
        private val log by LoggerDelegate()
    }

    @EventListener
    fun handle(indexingFinishedEvent: IndexingFinishedEvent) {
        log.info("Synchronizing lookups after indexing {} on {} has finished", indexingFinishedEvent.data.getId(), indexingFinishedEvent.data.source)
        synchronize()
    }

    @EventListener
    fun handle(obsoleteProjectsCleanedEvent: ObsoleteProjectsCleanedEvent) {
        log.info("Synchronizing lookups after obsolete projects were cleaned up")
        synchronize()
    }

    private fun synchronize() {
        if (lock.tryLock()) {
            try {
                lookupServices.forEach { it.refreshLookup() }
            } finally {
                lock.unlock()
            }
        } else {
            log.warn("Another lookups synchronization is in progress. Skipping..")
        }
    }
}