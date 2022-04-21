package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.project.Project

internal object Stars : NumericPowCheck() {
    override fun name(): String = Check.STARS

    override fun exponent(): Double = 1.toDouble() / 3

    override fun minValue(): Number = 5

    override fun readValue(project: Project): Number {
        return project.stats.stars ?: 0
    }

    override fun buildExplanation(featureValue: Number, score: Double, builder: Explanation.Builder) {
        builder.description("Stars")
            .addDetails("$score for ${featureValue.toInt()} stars")
    }
}
