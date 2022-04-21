package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.project.Project

// root of 5/2
internal object Forks : NumericPowCheck() {
    override fun exponent(): Double = .4

    override fun minValue(): Number = 1

    override fun readValue(project: Project): Number {
        return project.stats.forks ?: 0
    }

    override fun buildExplanation(featureValue: Number, score: Double, builder: Explanation.Builder) {
        builder.description("Forks")
            .addDetails("$score for ${featureValue.toInt()} forks")
    }

    override fun name(): String = Check.FORKS
}
