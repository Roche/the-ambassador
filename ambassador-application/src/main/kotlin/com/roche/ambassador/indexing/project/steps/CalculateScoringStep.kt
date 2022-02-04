package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.project.IndexingChain
import com.roche.ambassador.indexing.project.IndexingContext
import com.roche.ambassador.model.ScorecardCalculator
import com.roche.ambassador.model.ScorecardConfiguration
import org.springframework.stereotype.Component

@Component
internal class CalculateScoringStep(private val scorecardConfiguration: ScorecardConfiguration) : IndexingStep {

    companion object {
        private val log by LoggerDelegate()
    }

    private val calculator: ScorecardCalculator = ScorecardCalculator(scorecardConfiguration)

    override suspend fun handle(context: IndexingContext, chain: IndexingChain) {
        if (scorecardConfiguration.shouldCalculateScoring(context.project)) {
            log.debug("Calculating scoring for project '{}' (id={})", context.project.name, context.project.id)
            val scorecard = calculator.calculateFor(context.project)
            context.project.scorecard = scorecard
        } else {
            log.debug("Scoring will not be calculated for project '{}' (id={})", context.project.name, context.project.id)
        }
        chain.accept(context)
    }

    override fun getOrder(): Int = 5
}
