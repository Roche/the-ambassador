package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.extensions.LoggerDelegate
import kotlinx.coroutines.runBlocking
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextStoppedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class ContextEventHandlersForIndexing(
    val service: ProjectIndexingService
) {

    private val log by LoggerDelegate()

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