package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.extensions.toHumanReadable
import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.feature.CiExecutionsFeature
import com.roche.ambassador.model.feature.Features
import java.time.Duration

internal object CiPerformance : DurationCheck() {

    private const val MINUTES_5: Long = 60 * 5

    override fun name(): String = Check.CI_PERFORMANCE
    override fun degradationTime(): Long = MINUTES_5
    override fun maxTime(): Long = MINUTES_5 * 2

    override fun readValue(features: Features): Duration {
        return features.findValue(CiExecutionsFeature::class)
            .map { it.averageDurationAsSeconds() }
            .map { Duration.ofSeconds(it) }
            .orElseGet { Duration.ofSeconds(-1) }
    }

    override fun buildExplanation(duration: Duration, score: Double, builder: Explanation.Builder) {
        builder
            .description("CI performance")
            .addDetails("$score for ${duration.toHumanReadable()} of average pipeline duration.")
    }
}
