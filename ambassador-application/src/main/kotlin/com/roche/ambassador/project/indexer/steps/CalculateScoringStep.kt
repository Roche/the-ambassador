package com.roche.ambassador.project.indexer.steps

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.ScorecardCalculator
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(5)
class CalculateScoringStep : IndexingStep {

    companion object {
        private val log by LoggerDelegate()
    }

    private val calculator = ScorecardCalculator.withAllPolicies()

    override suspend fun handle(context: IndexingContext) {
        log.debug("Calculating scoring for project '{}' (id={})", context.project.name, context.project.id)
        val scorecard = calculator.calculateFor(context.project)
        context.project.scorecard = scorecard
    }
}