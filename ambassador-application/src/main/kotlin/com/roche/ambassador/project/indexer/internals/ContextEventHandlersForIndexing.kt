package com.roche.ambassador.project.indexer.internals

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.project.indexer.ProjectIndexingService
import com.roche.ambassador.storage.indexing.IndexingRepository
import kotlinx.coroutines.runBlocking
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.ContextStoppedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
internal class ContextEventHandlersForIndexing(
    val service: ProjectIndexingService,
    val indexingRepository: IndexingRepository
) {
    private val log by LoggerDelegate()

    @EventListener
    fun handleContextStartedEvent(event: ContextRefreshedEvent) {
        log.info("Cleaning up hanging indexing...")
        indexingRepository.findAllLocked().forEach { indexingRepository.save(it.unlock()) }
        indexingRepository.findAllInProgress().forEach { indexingRepository.save(it.fail()) }

    }

    @EventListener
    fun handleContextClosedEvent(event: ContextClosedEvent) {
        log.info("Terminating indexing due to context closing..")
        stopIndexing(true)
    }

    @EventListener
    fun handleContextClosedEvent(event: ContextStoppedEvent) {
        log.info("Gently stopping indexing due to context stopping..")
        stopIndexing()
    }

    private fun stopIndexing(forcibly: Boolean = false) {
        runBlocking {
            try {
                service.forciblyStopAll(forcibly)
            } catch(e: RuntimeException) {
                log.warn("Failed to stop indexing gently when received context event due to '{}'", e.message)
            }
        }
    }

}