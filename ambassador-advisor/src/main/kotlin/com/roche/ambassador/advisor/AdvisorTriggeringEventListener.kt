package com.roche.ambassador.advisor

import com.roche.ambassador.ConcurrencyProvider
import com.roche.ambassador.advisor.configuration.AdvisorProperties
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.events.ProjectIndexingFinishedEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class AdvisorTriggeringEventListener(
    private val advisorManager: AdvisorManager,
    concurrencyProvider: ConcurrencyProvider,
    private val advisorProperties: AdvisorProperties
) {

    private val coroutineScope = CoroutineScope(concurrencyProvider.getSupportingDispatcher() + SupervisorJob())

    companion object {
        private val log by LoggerDelegate()
    }

    @EventListener
    fun handleContextStartedEvent(event: ProjectIndexingFinishedEvent) {
        if (advisorProperties.isEnabled()) {
            val project = event.data
            log.debug("Received project indexing finished event for project {} (id={}). Starting advising flow...", project.name, project.id)
            coroutineScope.launch {
                advisorManager.giveAdvices(project)
            }
            log.debug("Advices for project {} (id={}) were given", project.name, project.id)
        }
    }
}