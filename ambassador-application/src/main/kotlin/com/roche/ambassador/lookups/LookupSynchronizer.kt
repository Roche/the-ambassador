package com.roche.ambassador.lookups

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.IndexingFinishedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
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