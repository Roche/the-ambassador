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

        //        ORGANIZATIONS_COUNT(1.0, 10),
        COMMIT_FREQUENCY(1.0, 1000),
        RECENT_RELEASES_COUNT(.5, 26),
        CLOSED_ISSUES_COUNT(.5, 5000),
        UPDATED_ISSUES_COUNT(.5, 5000),
        COMMENT_FREQUENCY(1.0, 15),
//        DEPENDENTS_COUNT(2.0, 500000)
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
        // @formatter:off
        return Score.builder("Criticality", features)
            .withFeature(IssuesFeature::class).sum({ issues -> UPDATED_ISSUES_COUNT.calc(issues.allIn90Days) }) { issues, partial -> "$partial for ${issues.allIn90Days} updated issues within 90 days" }
            .withFeature(IssuesFeature::class).sum({ issues -> CLOSED_ISSUES_COUNT.calc(issues.closedIn90Days) }) { issues, partial -> "$partial for ${issues.closedIn90Days} closed issues within 90 days" }
            .withFeature(ContributorsFeature::class).sum({ contributors -> CONTRIBUTORS_COUNT.calc(contributors.size) }) { contributors, partial -> "$partial for ${contributors.size} contributors" }
            .withFeature(LastActivityDateFeature::class).sum({ lastActivity -> LAST_UPDATED.calc(lastActivity.monthsUntilNow()) }) { lastActivity, partial -> "$partial for last activity at $lastActivity" }
            .withFeature(CreatedDateFeature::class).sum({ createdDate -> CREATED_SINCE.calc(createdDate.monthsUntilNow()) }) { createdDate, partial -> "$partial for creation date on $createdDate" }
//            .withFeature(OrganizationsFeature::class).sum { feature -> CREATED_SINCE.calc(feature.monthsUntilNow().toDouble()) }
            .withFeature(CommitsFeature::class).sum({ commitsTimeline -> COMMIT_FREQUENCY.calc(commitsTimeline.last(1).years().by().weeks().average()) }) { commits, partial -> "$partial for ${commits.last(1).years().by().weeks().average().round(2)} commits weekly average" }
            .withFeature(ReleasesFeature::class).sum({ releasesTimeline -> RECENT_RELEASES_COUNT.calc(releasesTimeline.last(1).years().sum()) }) { releases, partial -> "$partial for ${releases.last(1).years().sum()} releases in last year" }
            .withSubScore("comments")
            .withReason { partial, _ -> "$partial for comments frequency" }
            .withFeature(CommentsFeature::class).calculate { feature, _ -> feature.sum().toDouble() }
            .withFeature(IssuesFeature::class).calculate { feature, score -> COMMENT_FREQUENCY.calc(score / feature.allIn90Days) }
            .addNormalizer { it.round(2) }
            .reduce { aggScore, subScore -> aggScore + subScore }
//            .withFeature(DependentsFeature::class).sum { feature -> CREATED_SINCE.calc(feature.monthsUntilNow().toDouble()) }
            .addNormalizer { it / weightsTotal }
            .addNormalizer { it.round(4) }
            .addReasons("calculated as weighted average with $weightsTotal total weights")
            .build()
        // @formatter:on
    }
}
