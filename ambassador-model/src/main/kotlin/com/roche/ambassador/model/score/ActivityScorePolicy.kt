package com.roche.ambassador.model.score

import com.roche.ambassador.extensions.daysUntilNow
import com.roche.ambassador.model.Score
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.feature.*
import kotlin.math.*

object ActivityScorePolicy : ScorePolicy {

    private const val INITIAL_SCORE = 50.0
    override fun calculateScoreOf(features: Features): Score {
        val contributionScore = getContributionScore(features)
        val documentationScore = getDocumentationScore(features)
        return Score.zip("Activity", contributionScore, documentationScore) { s1, s2 ->
            val result = s1 + s2
            val logScaled = logScaleNormalizer().invoke(result)
            roundingNormalizer().invoke(logScaled)
        }
    }

    private fun getDocumentationScore(features: Features): Score {
        // @formatter:off
        return Score.builder("Documentation", features)
            .withFeature(ContributingGuideFeature::class).forFile(100, 100)
            .withFeature(ReadmeFeature::class).forFile(100, 100)
            .withFeature(DescriptionFeature::class)
                .filter { it.length >= 30 }
                .calculate { _, score -> score + 50 }
            .build()
        // @formatter:on
    }

    private fun getContributionScore(features: Features): Score {
        // @formatter:off
        return Score.builder("Contribution", features, false, INITIAL_SCORE)
            .withFeature(StarsFeature::class).calculate { starsFeature, score -> score + starsFeature * 2 }
            .withFeature(ForksFeature::class).calculate { forksFeature, score -> score + forksFeature * 5 }
            .withFeature(IssuesFeature::class).calculate { feature, score -> score + feature.open / 5 }
            // updated in last 3 months: adds a bonus multiplier between 0..1 to overall score (1 = updated today, 0 = updated more than 100 days ago)
            .withFeature(LastActivityDateFeature::class).calculate { feature, score -> score * (1 + (100 - min(feature.daysUntilNow(), 100).toDouble()) / 100) }
            .withFeature(CommitsFeature::class)
            // average commits: adds a bonus multiplier between 0..1 to overall score (1 => 10 commits per week, 0 = less than 3 commits per week)
                .calculate { commitsFeature, score ->
                    val avg = commitsFeature.by().weeks().average()
                    score * (1 + min(max(avg - 3, .0), 7.0) / 7)
                }
            // average commits: adds a bonus multiplier between 0..1 to overall score (1 = >10 commits per week, 0 = less than 3 commits per week)
            .withSubScore("Young project boost")
                // all repositories updated in the previous year will receive a boost of maximum 1000 declining by days since last update
                .withFeature(LastActivityDateFeature::class).calculate { feature, _ -> (1000 - min(feature.daysUntilNow(), 365).toDouble() * 2.74) }
                // gradually scale down boost according to repository creation date to mix with "real" engagement stats
                .withFeature(CreatedDateFeature::class).calculate { feature, score -> score * (365 - min(feature.daysUntilNow(), 365).toDouble()) / 365 }
                .addNormalizer { abs(it) } // sometimes calculations return negative zero, making comparison (0.0d).equals(-0.0d) fail
                .reduce { aggScore, subScore -> aggScore + max(subScore, 0.0) }
            // penalize private projects
            .withFeature(VisibilityFeature::class)
                .filter { it == Visibility.PRIVATE }
                .calculate { _, score -> score * .3 }
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
