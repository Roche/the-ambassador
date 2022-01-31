package com.roche.ambassador.model.score

import com.roche.ambassador.extensions.daysUntilNow
import com.roche.ambassador.extensions.round
import com.roche.ambassador.model.Score
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.feature.*
import kotlin.math.*

object ActivityScorePolicy : ScorePolicy {

    private const val INITIAL_SCORE = 50.0
    override fun calculateScoreOf(features: Features): Score {
        val contributionScore = getContributionScore(features)
        val documentationScore = getDocumentationScore(features)
        return Score.zip("Activity", contributionScore, documentationScore) { s1, s2, explanationBuilder ->
            explanationBuilder.description("")
                .addDetails("$s1 for contribution")
                .addDetails("$s2 for documentation")
                .addDetails("subtracted $INITIAL_SCORE of initialization value")
                .maxValue(5000.0)
            val result = s1 + s2
            val logScaled = logScaleNormalizer().invoke(result)
            roundingNormalizer().invoke(logScaled)
        }
    }

    private fun getDocumentationScore(features: Features): Score {
        // @formatter:off
        return Score.builder("Documentation", features)
            .withFeature(ContributingGuideFeature::class).forFile(100, 100) { _, partial -> "$partial for existence of contributing guide file"}
            .withFeature(ReadmeFeature::class).forFile(100, 100) { _, partial -> "$partial for existence of README file"}
            .withFeature(DescriptionFeature::class)
            .filter { it.length >= 30 }
            .sum({ 50.0 }) { _, partial -> "$partial for existence of description"}
            .build()
        // @formatter:on
    }

    private fun getContributionScore(features: Features): Score {
        // @formatter:off
        return Score.builder("Contribution", features, false, INITIAL_SCORE)
            .addReasons("$INITIAL_SCORE for initial score")
            .withFeature(StarsFeature::class).sum({ stars -> stars.toDouble() * 2 }) { stars, partial -> "$partial for $stars stars" }
            .withFeature(ForksFeature::class).sum({ forks -> forks.toDouble() * 5 }) { forks, partial -> "$partial for $forks forks"}
            .withFeature(IssuesFeature::class).sum({ issues -> issues.open.toDouble() / 5 }) { issues, partial -> "$partial for ${issues.open} open issues"}
            // updated in last 3 months: adds a bonus multiplier between 0..1 to overall score (1 = updated today, 0 = updated more than 100 days ago)
            .withFeature(LastActivityDateFeature::class).multiply({ lastActivityDate -> (1 + (100 - min(lastActivityDate.daysUntilNow(), 100).toDouble()) / 100) }) { lastActivityDate, partial -> "multiplied by $partial for last activity date at $lastActivityDate"}
            .withFeature(CommitsFeature::class)
            // average commits: adds a bonus multiplier between 0..1 to overall score (1 => 10 commits per week, 0 = less than 3 commits per week)
            .multiply({ commitsFeature ->
                          val avg = commitsFeature.by().weeks().average()
                          (1 + min(max(avg - 3, .0), 7.0) / 7)
                      }) { commits, partial -> "multiplied by $partial for ${commits.by().weeks().average().round(2)} weekly commits average"}
            // average commits: adds a bonus multiplier between 0..1 to overall score (1 = >10 commits per week, 0 = less than 3 commits per week)
            .withSubScore("Young project boost")
                .withReason { boost, _ -> "young project boosted by $boost" }
                // all repositories updated in the previous year will receive a boost of maximum 1000 declining by days since last update
                .withFeature(LastActivityDateFeature::class).calculate { feature, _ -> (1000 - min(feature.daysUntilNow(), 365).toDouble() * 2.74) }
                // gradually scale down boost according to repository creation date to mix with "real" engagement stats
                .withFeature(CreatedDateFeature::class).calculate { feature, score -> score * (365 - min(feature.daysUntilNow(), 365).toDouble()) / 365 }
                .addNormalizer { abs(it) } // sometimes calculations return negative zero, making comparison (0.0d).equals(-0.0d) fail
                .reduce { aggScore, subScore -> aggScore + max(subScore, 0.0) }
            // penalize private projects
            .withFeature(VisibilityFeature::class)
                .filter { it == Visibility.PRIVATE }
                .multiply({ .3 }) { _, penalty -> "multiplying total score by $penalty due to private visibility"}
            .build()
        // @formatter:on
    }

    // build in a logarithmic scale for very active projects (open ended but stabilizing around 5000)
    private fun logScaleNormalizer(): ScoreNormalizer = {
        if (it > 3000) {
            3000 + ln(it) * 100
        } else {
            it
        }
    }

    // final score is a rounded value starting from 0 (subtract the initial value)
    private fun roundingNormalizer(): ScoreNormalizer = {
        max(round(it - INITIAL_SCORE), 0.0)
    }
}
