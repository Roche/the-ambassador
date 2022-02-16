package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.feature.ReadmeFeature
import java.util.*

internal object Readme : StringLengthCheck() {

    private const val MIN_LENGTH = 300

    override fun name(): String = Check.README
    override fun minLength(): Int = MIN_LENGTH

    override fun readStringLength(features: Features): Optional<Int> = features.findValue(ReadmeFeature::class)
        .map { it.contentLength }
        .map { it!!.toInt() }

    override fun buildExplanation(featureValue: Int, score: Double, builder: Explanation.Builder) {
        builder
            .description("Readme")
            .addDetails("$score for readme with $featureValue chars length.")
    }
}