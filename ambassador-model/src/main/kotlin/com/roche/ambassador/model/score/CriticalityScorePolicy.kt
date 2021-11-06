package com.roche.ambassador.model.score

import com.roche.ambassador.extensions.monthsUntilNow
import com.roche.ambassador.extensions.round
import com.roche.ambassador.model.Score
import com.roche.ambassador.model.feature.*
import com.roche.ambassador.model.score.CriticalityScorePolicy.CriticalityCheck.*
import com.roche.ambassador.model.score.CriticalityScorePolicy.CriticalityCheck.Companion.weightsTotal
import kotlin.math.log10
import kotlin.math.max

// https://github.com/ossf/criticality_score
object CriticalityScorePolicy : ScorePolicy {

    internal enum class CriticalityCheck(val weight: Double, val threshold: Int) {
        CREATED_SINCE(1.0, 120),
        LAST_UPDATED(-1.0, 120),
        CONTRIBUTORS_COUNT(2.0, 5000),
        ORGANIZATIONS_COUNT(1.0, 10),
        COMMIT_FREQUENCY(1.0, 1000),
        RECENT_RELEASES_COUNT(.5, 26),
        CLOSED_ISSUES_COUNT(.5, 5000),
        OPENED_ISSUES_COUNT(.5, 5000),
        COMMENT_FREQUENCY(1.0, 15),
        DEPENDENTS_COUNT(2.0, 500000)
        ;

        fun calc(value: Double): Double {
            return weight * log10(1 + value) / log10(1 + max(value, threshold.toDouble()))
        }

        fun calc(value: Int): Double = calc(value.toDouble())

        fun calc(value: Long): Double = calc(value.toDouble())

        companion object {
            val weightsTotal: Double = values()
                .map { it.weight }
                .reduce { acc, weight -> acc + weight }
        }
    }

    override fun calculateScoreOf(features: Features): Score {
        return Score.builder("Criticality", features)
            .withFeature(IssuesFeature::class).calculate { feature, score -> score + OPENED_ISSUES_COUNT.calc(feature.value().get().openedIn90Days) }
            .withFeature(IssuesFeature::class).calculate { feature, score -> score + CLOSED_ISSUES_COUNT.calc(feature.value().get().closedIn90Days) }
            .withFeature(ContributorsFeature::class).calculate { feature, score -> score + CONTRIBUTORS_COUNT.calc(feature.value().get().size) }
            .withFeature(LastActivityDateFeature::class).calculate { feature, score -> score + LAST_UPDATED.calc(feature.value().get().monthsUntilNow()) }
            .withFeature(CreatedDateFeature::class).calculate { feature, score -> score + CREATED_SINCE.calc(feature.value().get().monthsUntilNow()) }
//            .withFeature(OrganizationsFeature::class).calculate { feature, score -> score + CREATED_SINCE.calc(feature.value().get().monthsUntilNow().toDouble()) }
            .withFeature(CommitsFeature::class).calculate { feature, score -> score + COMMIT_FREQUENCY.calc(feature.value().get().last(1).years().by().weeks().average()) }
            .withFeature(ReleasesFeature::class).calculate { feature, score -> score + RECENT_RELEASES_COUNT.calc(feature.value().get().last(1).years().sum()) }
//            .withFeature(CommentsFeature::class).calculate { feature, score -> score + CREATED_SINCE.calc(feature.value().get().monthsUntilNow().toDouble()) }
//            .withFeature(DependentsFeature::class).calculate { feature, score -> score + CREATED_SINCE.calc(feature.value().get().monthsUntilNow().toDouble()) }
            .addNormalizer { it / weightsTotal }
            .addNormalizer { it.round(4) }
            .build()
    }
}
