package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.indexing.project.IndexingChain
import com.roche.ambassador.indexing.project.IndexingContext
import com.roche.ambassador.model.events.ProjectIndexingFinishedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class ProjectIndexedEventPublisherStep(private val eventPublisher: ApplicationEventPublisher) : IndexingStep {
    override suspend fun handle(context: IndexingContext, chain: IndexingChain) {
        val event = ProjectIndexingFinishedEvent(context.project)
        eventPublisher.publishEvent(event)
        chain.accept(context)
    }

    override fun getOrder(): Int = 7
}
