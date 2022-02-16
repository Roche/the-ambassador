package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.feature.CiExecutionsFeature
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.score.quality.PartialCheckResult
import com.roche.ambassador.model.project.ci.CiStability as ProjectCiStability

internal object CiStability : BaseCheck<ProjectCiStability>() {
    override fun name(): String = Check.CI_STABILITY

    override fun readValue(features: Features): ProjectCiStability {
        return features.findValue(CiExecutionsFeature::class)
            .map { it.stability }
            .map { it!! }
            .orElseGet { ProjectCiStability(0.0, "0%", 0, 0, 0, ProjectCiStability.State.NONE) }
    }

    override fun calculateScore(stability: ProjectCiStability): Double {
        return if (stability.total <= 0) {
            Check.MIN_SCORE
        } else {
            PartialCheckResult.proportional(stability.successful, stability.failed + stability.successful)
        }
    }

    override fun buildExplanation(stability: ProjectCiStability, score: Double, builder: Explanation.Builder) {
        val details = if (stability.state == ProjectCiStability.State.NONE) {
            "No CI executions were found, thus unable to calculate stability."
        } else {
            "$score for ${stability.valuePercentage} of successful builds in last ${stability.failed + stability.successful} builds."
        }
        builder.description("CI stability")
            .addDetails(details, "State: ${stability.state}")
    }
}