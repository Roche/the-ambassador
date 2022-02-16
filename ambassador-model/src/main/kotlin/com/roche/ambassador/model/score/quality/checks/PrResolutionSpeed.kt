package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.extensions.toHumanReadable
import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.feature.PullRequestsFeature
import java.time.Duration

internal object PrResolutionSpeed : DurationCheck() {

    private const val DAY_1: Long = 60 * 60 * 24

    override fun name(): String = Check.PR_RESOLUTION_SPEED
    override fun degradationTime(): Long = DAY_1

    override fun maxTime(): Long = DAY_1

    override fun readValue(features: Features): Duration {
        return features.findValue(PullRequestsFeature::class)
            .map { it.averageTimeToMergeAsSeconds() }
            .map { Duration.ofSeconds(it) }
            .orElseGet { Duration.ofSeconds(-1) }
    }

    override fun buildExplanation(featureValue: Duration, score: Double, builder: Explanation.Builder) {
        builder
            .description("Pull Request resolution speed")
            .addDetails("$score for ${featureValue.toHumanReadable()} of average time to merge pull request.")
    }
}