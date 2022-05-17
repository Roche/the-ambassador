package com.roche.ambassador.advisor.rules

import com.roche.ambassador.advisor.dsl.RulesBuilder
import com.roche.ambassador.advisor.dsl.Then
import com.roche.ambassador.extensions.formatAsFileSize
import com.roche.ambassador.extensions.toHumanReadable
import com.roche.ambassador.model.feature.CiExecutionsFeature
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.project.ci.CiStability
import org.springframework.util.unit.DataSize
import java.time.Duration

object BaseCiRules : CiRule {

    override fun RulesBuilder.apply() {
        matchFirst({ stats.jobArtifactsSize ?: -1 }) {
            that { this >= config.ci.artifactsSize.high } then "size.artifacts.high" withJobArtifactsSizeAnd config.ci.artifactsSize.high
            that { this >= config.ci.artifactsSize.med } then "size.artifacts.medium" withJobArtifactsSizeAnd config.ci.artifactsSize.med
            that { this >= config.ci.artifactsSize.low } then "size.artifacts.low" withJobArtifactsSizeAnd config.ci.artifactsSize.low
        }

        whenEnabled(config.ci.stability) {
            matchFirst(CiExecutionsFeature::class, { stability?.state }) {
                that { this == CiStability.State.OCCASIONALLY_FAILING } then "ci.stability.occasionally-failing" with { getStability() }
                that { this == CiStability.State.UNSTABLE } then "ci.stability.unstable" with { getStability() }
                that { this == CiStability.State.CRITICALLY_UNSTABLE } then "ci.stability.critically-unstable" with { getStability() }
            }
        }

        whenEnabled(config.ci.performance) {
            matchFirst(CiExecutionsFeature::class, { averageDurationAsSeconds() }) {
                that { this >= config.ci.performance.ranges.high.seconds } then "ci.performance.critical" withCiAverageDurationAnd config.ci.performance.ranges.high
                that { this >= config.ci.performance.ranges.med.seconds } then "ci.performance.very-low" withCiAverageDurationAnd config.ci.performance.ranges.med
                that { this >= config.ci.performance.ranges.low.seconds } then "ci.performance.low" withCiAverageDurationAnd config.ci.performance.ranges.low
            }
        }
    }

    private fun Project.getStability(): String {
        // TODO improve `with` statement so that it can use context of surrounding clause, e.g. here reuse feature value
        return features.findValue(CiExecutionsFeature::class)
            .map { it.stability?.valuePercentage }
            .orElse("_unknown_")!!
    }

    private fun Project.getAverageDuration(): String {
        return features.findValue(CiExecutionsFeature::class)
            .map { it.averageDurationAsString()}
            .orElse("_unknown_")!!
    }

    private fun DataSize.formatAsFileSize(): String = toBytes().formatAsFileSize()

    private infix fun Then.withCiAverageDurationAnd(other: Duration) {
        with { listOf(getAverageDuration(), other.toHumanReadable()) }
    }

    private infix fun Then.withJobArtifactsSizeAnd(other: DataSize) {
        with { listOf(stats.jobArtifactsSize!!.formatAsFileSize(), other.formatAsFileSize()) }
    }
}
