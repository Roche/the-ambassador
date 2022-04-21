package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.project.Project

internal object CanCreatePr : BooleanCheck() {

    override fun readValue(project: Project): Boolean {
        return project.permissions.pullRequests.canEveryoneAccess()
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
