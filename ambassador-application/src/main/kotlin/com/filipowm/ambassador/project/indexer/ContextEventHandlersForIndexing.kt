package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.storage.indexing.IndexingRepository
import kotlinx.coroutines.runBlocking
import org.springframework.context.event.*
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
            service.forciblyStop(forcibly)
        }
    }

}