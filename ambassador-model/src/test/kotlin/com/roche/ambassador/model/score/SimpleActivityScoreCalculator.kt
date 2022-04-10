package com.roche.ambassador.model.score

import com.roche.ambassador.extensions.daysUntilNow
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.dataproviders.nowDate
import com.roche.ambassador.model.extensions.toExcerptFile
import com.roche.ambassador.model.extensions.toRawFile
import com.roche.ambassador.model.feature.*
import com.roche.ambassador.model.files.Documentation
import com.roche.ambassador.model.project.Issues
import com.roche.ambassador.model.stats.Timeline
import java.time.LocalDate
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

/**
 * Follows: https://github.com/InnerSourceCommons/InnerSourcePatterns/blob/main/patterns/2-structured/repository-activity-score.md#solutions
 */
object SimpleActivityScoreCalculator : TestCalculator<ActivityData> {

    const val INITIAL_SCORE = 50.0

    override fun calculate(data: ActivityData): Double {
        var score = INITIAL_SCORE
        score += data.forks * 5
        score += data.stars * 2 // multiply by 2 instead of dividing by 3
        score += data.openIssues / 5
        val daysSinceLastUpdate = data.lastActivityDate.daysUntilNow().toDouble()
        score *= (1 + (100 - min(daysSinceLastUpdate, 100.0)) / 100)

        if (data.commitsTimeline != null) {
            val avg = data.commitsTimeline.last(3).months().average()
            score *= (1 + min(max(avg - 3, 0.0), 7.0) / 7)
        }

        var boost = (1000 - min(daysSinceLastUpdate, 365.0) * 2.74)
        boost *= (365 - min(data.createdDate.daysUntilNow().toDouble(), 365.0)) / 365
        score += boost

        if (data.private) {
            score *= .3
        }

        score += boostForDocumentation(data.readme, 100, 100)
        score += boostForDocumentation(data.contributionGuide, 100, 100)
        if (data.description != null && data.description.length >= 30) {
            score += 50
        }

        if (score > 3000) {
            score = 3000 + ln(score) * 100
        }
        score = max(round(score - INITIAL_SCORE), 0.0)
        return score
    }

    private fun boostForDocumentation(documentation: Documentation, minLength: Int = 100, boost: Int = 100) =
        if (documentation.exists && documentation.contentLength!! >= minLength) {
            boost
        } else {
            0
        }
}

data class ActivityData(
    val forks: Int = 0,
    val stars: Int = 0,
    val openIssues: Int = 0,
    val lastActivityDate: LocalDate = nowDate().minusYears(5),
    val createdDate: LocalDate = nowDate().minusYears(6),
    val private: Boolean = false,
    val readme: Documentation = Documentation.notExistent(),
    val contributionGuide: Documentation = Documentation.notExistent(),
    val license: Documentation = Documentation.notExistent(),
    val description: String? = null,
    val commitsTimeline: Timeline? = Timeline()
) {

    fun toFeatures(): Features = Features(
        forksFeature(), starsFeature(), lastActivityFeature(),
        createdDateFeature(), visibilityFeature(), readmeFeature(), contributionGuideFeature(),
        licenseFeature(), descriptionFeature(), commitsFeature(), issuesFeature()
    )

    fun visibility(): Visibility = if (private) {
        Visibility.PRIVATE
    } else {
        Visibility.PUBLIC
    }

    fun forksFeature(): ForksFeature = ForksFeature(forks)
    fun starsFeature(): StarsFeature = StarsFeature(stars)
    fun lastActivityFeature(): LastActivityDateFeature = LastActivityDateFeature(lastActivityDate)
    fun createdDateFeature(): CreatedDateFeature = CreatedDateFeature(createdDate)
    fun visibilityFeature(): VisibilityFeature = VisibilityFeature(visibility())
    fun readmeFeature(): ReadmeFeature = ReadmeFeature(readme.toExcerptFile())
    fun contributionGuideFeature(): ContributingGuideFeature = ContributingGuideFeature(contributionGuide.toRawFile())
    fun licenseFeature(): LicenseFeature = LicenseFeature(license.toRawFile())
    fun descriptionFeature(): DescriptionFeature = DescriptionFeature(description)
    fun commitsFeature(): CommitsFeature = CommitsFeature(commitsTimeline)
    fun issuesFeature(): IssuesFeature = IssuesFeature(Issues(openIssues, openIssues, 0, 0, 0, 0))
}
