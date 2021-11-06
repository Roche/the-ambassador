package com.roche.ambassador.model

import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.score.ScorePolicy

class ScorecardCalculator(private val policies: Set<ScorePolicy>) {

    fun calculateFor(project: Project): Scorecard {
        val scores = policies.map { it.calculateScoreOf(project.features) }.toSet()
        if (project.features.isEmpty()) {
            return Scorecard.notCalculated(project)
        } else if (project.scorecard != null && project.scorecard!!.isCalculated()) {
            return project.scorecard!!
        }
        // as of now calculate final score as a multiplication of all scores
        val score = scores.map { it.value() }.reduce { one, two -> one * two }
        return Scorecard.of(project, score, scores)
    }
}
