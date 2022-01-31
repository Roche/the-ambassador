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
internal class LanguagesSynchronizer(private val languagesService: LanguagesService) {

    private val lock: Lock = ReentrantLock()

    companion object {
        private val log by LoggerDelegate()
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handle(indexingFinishedEvent: IndexingFinishedEvent) {
        log.info("Synchronizing languages after indexing {} on {} has finished", indexingFinishedEvent.data.getId(), indexingFinishedEvent.data.source)
        if (lock.tryLock()) {
            try {
                languagesService.synchronizeLookup()
            } finally {
                lock.unlock()
            }
        } else {
            log.warn("Another topics synchronization is in progress. Skipping..")
        }
    }
}
