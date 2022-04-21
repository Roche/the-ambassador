package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.feature.ForksFeature

// root of 5/2
internal object Forks : NumericPowCheck() {
    override fun exponent(): Double = .4

    override fun minValue(): Number = 1

    override fun readValue(features: Features): Number {
        return features.findValue(ForksFeature::class).orElse(0)
    }

    override fun buildExplanation(featureValue: Number, score: Double, builder: Explanation.Builder) {
        builder.description("Forks")
            .addDetails("$score for ${featureValue.toInt()} forks")
    }

    override fun name(): String = Check.FORKS
}
