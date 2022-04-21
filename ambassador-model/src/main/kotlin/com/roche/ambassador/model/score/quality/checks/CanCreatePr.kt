package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.feature.PermissionsFeature

internal object CanCreatePr : BooleanCheck() {

    override fun readValue(features: Features): Boolean {
        return features.findValue(PermissionsFeature::class)
            .map { it.pullRequests.canEveryoneAccess() }
            .orElse(false)
    }

    override fun buildExplanation(featureValue: Boolean, score: Double, builder: Explanation.Builder) {
        val auxiliary = if (featureValue) {
            "everyone"
        } else {
            "noone"
        }
        builder.description("Can everyone create Pull Request")
            .addDetails("$score because $auxiliary can create pull request to this project.")
    }

    override fun name(): String = Check.CAN_CREATE_PR
}
