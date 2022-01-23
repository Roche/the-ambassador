package com.roche.ambassador.model

import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.score.ActivityScorePolicy
import com.roche.ambassador.model.score.CriticalityScorePolicy
import com.roche.ambassador.model.score.ScorePolicy
import com.roche.ambassador.model.score.quality.QualityScorePolicy

class ScorecardCalculator(configuration: ScorecardConfiguration) {

    private val policies: List<ScorePolicy>

    init {
        val policies = mutableListOf<ScorePolicy>()
        if (configuration.activity.enabled) {
            policies += ActivityScorePolicy
        }
        if (configuration.criticality.enabled) {
            policies += CriticalityScorePolicy
        }
        if (configuration.quality.enabled) {
            policies += QualityScorePolicy(
                configuration.quality.getEnabledChecks(),
                configuration.quality.getEnabledChecksConfiguration(),
                configuration.quality.experimental
            )
        }
        this.policies = policies.toList()
    }

    fun calculateFor(project: Project): Scorecard {
        val scores = policies.map { it.calculateScoreOf(project) }.toSet()
        if (project.features.isEmpty()) {
            return Scorecard.notCalculated(project)
        } else if (project.scorecard != null && project.scorecard!!.isCalculated()) {
            return project.scorecard!!
        }
        // as of now calculate final score as a multiplication of all non-experimental scores
        val score = scores
            .filterNot { it.isExperimental() }
            .map { it.value() }
            .reduce { one, two -> one * two }
        return Scorecard.of(project, score, scores)
    }
}
