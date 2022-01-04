package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.indexing.project.IndexingContext
import com.roche.ambassador.model.events.ProjectIndexingFinishedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(7)
internal class ProjectIndexedEventPublisherStep(private val eventPublisher: ApplicationEventPublisher) : IndexingStep {
    override suspend fun handle(context: IndexingContext) {
        val event = ProjectIndexingFinishedEvent(context.project)
        eventPublisher.publishEvent(event)
    }
}
