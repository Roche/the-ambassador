package com.roche.ambassador.indexing.project

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.project.steps.IndexingStep
import kotlin.reflect.KClass

class IndexingChain(private val steps: List<IndexingStep>) {

    private val nextStepsLookup: Map<KClass<out IndexingStep>, IndexingStep>

    companion object {
        private val log by LoggerDelegate()
    }

    init {
        var current = steps.first()
        val lookup: MutableMap<KClass<out IndexingStep>, IndexingStep> = mutableMapOf()
        for (next in steps.drop(1)) {
            lookup[current::class] = next
            current = next
        }
        this.nextStepsLookup = lookup.toMap()
    }

    suspend fun accept(context: IndexingContext) {
        val currentStep = context.currentStep
        val nextStep = if (currentStep == null) {
            steps[0]
        } else {
            nextStepsLookup.getOrDefault(currentStep, null)
        }
        if (nextStep == null) {
            log.info("Indexing chain has finished for project '{}' (id={})", context.project.fullName, context.project.id)
        } else {
            log.debug("Step {} will handle project '{}' (id={})", nextStep::class.simpleName, context.project.fullName, context.project.id)
            context.currentStep = nextStep::class
            nextStep.handle(context, this)
        }
    }
}
