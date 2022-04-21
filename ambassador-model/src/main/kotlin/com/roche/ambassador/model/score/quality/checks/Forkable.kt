package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.project.Project

internal object Forkable : BooleanCheck() {

    override fun readValue(project: Project): Boolean {
        return project.permissions.forks.canEveryoneAccess()
    }

    override fun buildExplanation(featureValue: Boolean, score: Double, builder: Explanation.Builder) {
        val auxiliary = if (featureValue) {
            "everyone"
        } else {
            "noone"
        }
        builder
            .description("Forkable")
            .addDetails("$score because $auxiliary can create fork this project.")
    }

    override fun name(): String = Check.FORKABLE
}
