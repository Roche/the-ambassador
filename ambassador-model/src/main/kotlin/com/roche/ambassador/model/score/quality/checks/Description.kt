package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.feature.DescriptionFeature
import com.roche.ambassador.model.feature.Features
import java.util.*

internal object Description : StringLengthCheck() {

    private const val MIN_LENGTH: Int = 10

    override fun name(): String = Check.DESCRIPTION
    override fun minLength(): Int = MIN_LENGTH
    override fun readStringLength(features: Features): Optional<Int> = features.findValue(DescriptionFeature::class).map { it.length }

    override fun buildExplanation(featureValue: Int, score: Double, builder: Explanation.Builder) {
        builder
            .description("Project description")
            .addDetails("$score for description with $featureValue chars length.")
    }
}
