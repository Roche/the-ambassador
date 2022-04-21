package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.feature.VisibilityFeature
import com.roche.ambassador.model.Visibility as ProjectVisibility

internal object Visibility : BaseCheck<ProjectVisibility>() {
    override fun name(): String = Check.VISIBILITY

    override fun readValue(features: Features): ProjectVisibility {
        return features.findValue(VisibilityFeature::class).orElse(ProjectVisibility.UNKNOWN)
    }

    override fun calculateScore(featureValue: ProjectVisibility): Double {
        return if (featureValue.getThisAndLessStrict().contains(ProjectVisibility.INTERNAL)) {
            Check.MAX_SCORE
        } else {
            Check.MIN_SCORE
        }
    }

    override fun buildExplanation(featureValue: ProjectVisibility, score: Double, builder: Explanation.Builder) {
        builder
            .description("Visibility")
            .addDetails("$score because project visibility is ${featureValue.name.lowercase()}")
    }
}
