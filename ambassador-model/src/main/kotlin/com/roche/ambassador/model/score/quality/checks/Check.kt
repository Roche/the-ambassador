package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.extensions.round
import com.roche.ambassador.extensions.roundToHalf
import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.score.quality.PartialCheckResult
import java.time.Duration
import java.util.*
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

sealed interface Check {

    companion object {
        const val MAX_SCORE = 10.0
        const val MIN_SCORE = 0.0
        const val MIN_CONFIDENCE = 0
        const val MAX_CONFIDENCE = 10
        const val BRANCH_PROTECTION = "branch-protection"
        const val CAN_CREATE_PR = "can-create-pr"
        const val CI_PERFORMANCE = "ci-performance"
        const val CI_STABILITY = "ci-stability"
        const val CONTRIBUTION_GUIDE = "contribution-guide"
        const val CONTRIBUTORS = "contributors"
        const val CREATED_BOOST = "created-date"
        const val DESCRIPTION = "description"
        const val FORKABLE = "forkable"
        const val FORKS = "forks"
        const val ISSUES_CLOSED = "issues-closed"
        const val ISSUES_OPEN = "issues-open"
        const val ISSUES_UPDATED = "issues-updated"
        const val LICENSE = "license"
        const val MAINTAINED = "maintained"
        const val OWNERSHIP_BUS_FACTOR = "ownership-bus-factor"
        const val PR_RESOLUTION_SPEED = "pr-resolution-speed"
        const val README = "readme"
        const val RELEASES = "releases"
        const val STARS = "stars"
        const val TOPICS = "topics"
        const val UPDATED_PENALTY = "updated-date"
        const val VISIBILITY = "visibility"
    }

    fun name(): String
    fun check(features: Features): PartialCheckResult
}

object ChecksRegistry {

    private val checks: Map<String, Check> = mapOf(
        Check.BRANCH_PROTECTION to BranchProtection,
        Check.CAN_CREATE_PR to CanCreatePr,
        Check.CI_PERFORMANCE to CiPerformance,
        Check.CI_STABILITY to CiStability,
        Check.CONTRIBUTION_GUIDE to ContributionGuide,
        Check.CREATED_BOOST to CreatedDate,
        Check.DESCRIPTION to Description,
        Check.FORKABLE to Forkable,
        Check.FORKS to Forks,
        Check.OWNERSHIP_BUS_FACTOR to OwnershipBusFactor,
        Check.PR_RESOLUTION_SPEED to PrResolutionSpeed,
        Check.README to Readme,
        Check.TOPICS to Topics,
        Check.STARS to Stars,
        Check.UPDATED_PENALTY to UpdatedDate,
        Check.VISIBILITY to Visibility,
    )

    operator fun get(name: String): Optional<Check> {
        return Optional.ofNullable(checks[name])
    }
}

internal sealed class BaseCheck<T> : Check {

    abstract fun readValue(features: Features): T
    abstract fun calculateScore(featureValue: T): Double
    abstract fun buildExplanation(featureValue: T, score: Double, builder: Explanation.Builder)

    override fun check(features: Features): PartialCheckResult {
        val featureValue = readValue(features)
        val score = calculateScore(featureValue)
        val adjustedScore = min(score, Check.MAX_SCORE).roundToHalf()
        return PartialCheckResult.builder(name())
            .score(adjustedScore)
            .explanation {
                it.value(adjustedScore)
                buildExplanation(featureValue, adjustedScore, it)
            }
            .build()
    }
}

internal sealed class BooleanCheck : BaseCheck<Boolean>() {
    override fun calculateScore(featureValue: Boolean): Double {
        return if (featureValue) {
            Check.MAX_SCORE
        } else {
            Check.MIN_SCORE
        }
    }
}

internal sealed class StringLengthCheck : BaseCheck<Int>() {

    abstract fun minLength(): Int

    abstract fun readStringLength(features: Features): Optional<Int>

    override fun readValue(features: Features): Int = readStringLength(features).orElse(0)

    override fun calculateScore(featureValue: Int): Double {
        return if (featureValue < minLength()) {
            Check.MIN_SCORE
        } else {
            val adjusted = featureValue - minLength()
            floor(sqrt(adjusted.toDouble()))
        }
    }
}

internal sealed class DurationCheck : BaseCheck<Duration>() {

    abstract fun degradationTime(): Long
    abstract fun maxTime(): Long

    override fun calculateScore(featureValue: Duration): Double {
        return if (featureValue.isNegative || featureValue.isZero) {
            Check.MIN_SCORE
        } else {
            val durationSeconds = featureValue.seconds
            val diff = durationSeconds - maxTime()
            if (diff <= 0) {
                Check.MAX_SCORE
            } else {
                Check.MAX_SCORE - min(floor(diff.toDouble() / degradationTime()), Check.MAX_SCORE)
            }
        }
    }
}

internal sealed class NumericPowCheck : BaseCheck<Number>() {

    abstract fun exponent(): Double

    abstract fun minValue(): Number

    override fun calculateScore(featureValue: Number): Double {
        val double = featureValue.toDouble()
        val minValue = minValue().toDouble()
        val diff = double - minValue
        if (diff < 0) {
            return Check.MIN_SCORE
        }
        val pow = diff.pow(exponent()).round(2)
        val floor = floor(pow)
        return min(floor, Check.MAX_SCORE)
    }
}
