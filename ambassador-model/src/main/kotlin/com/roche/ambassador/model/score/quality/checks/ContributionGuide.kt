package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.feature.ContributingGuideFeature
import com.roche.ambassador.model.feature.Features
import java.util.*

internal object ContributionGuide : StringLengthCheck() {

    private const val MIN_LENGTH = 300

    override fun name(): String = Check.CONTRIBUTION_GUIDE
    override fun minLength(): Int = MIN_LENGTH

    override fun readStringLength(features: Features): Optional<Int> = features.findValue(ContributingGuideFeature::class)
        .map { it.contentLength }
        .map { it!!.toInt() }

    override fun buildExplanation(featureValue: Int, score: Double, builder: Explanation.Builder) {
        builder
            .description("Contribution Guide")
            .addDetails("$score for contribution guide with $featureValue chars length.")
    }
}