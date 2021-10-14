package com.filipowm.ambassador.model.score

import com.filipowm.ambassador.extensions.monthsUntilNow
import com.filipowm.ambassador.extensions.round
import com.filipowm.ambassador.model.dataproviders.ContributorGenerator
import com.filipowm.ambassador.model.feature.*
import com.filipowm.ambassador.model.project.Issues
import com.filipowm.ambassador.model.score.CriticalityScorePolicy.CriticalityCheck.Companion.weightsTotal
import com.filipowm.ambassador.model.stats.Timeline
import java.time.LocalDate

object SimpleCriticalityScoreCalculator : TestCalculator<CriticalityData> {
    override fun calculate(data: CriticalityData): Double {
        var score = 0.0
        score += CriticalityScorePolicy.CriticalityCheck.OPENED_ISSUES_COUNT.calc(data.openedIssuesIn90Days)
        score += CriticalityScorePolicy.CriticalityCheck.CLOSED_ISSUES_COUNT.calc(data.closedIssuesIn90Days)
        score += CriticalityScorePolicy.CriticalityCheck.CONTRIBUTORS_COUNT.calc(data.contributorsCount)
        score += CriticalityScorePolicy.CriticalityCheck.LAST_UPDATED.calc(data.lastActivityDate.monthsUntilNow())
        score += CriticalityScorePolicy.CriticalityCheck.CREATED_SINCE.calc(data.createdDate.monthsUntilNow())
        score += CriticalityScorePolicy.CriticalityCheck.COMMIT_FREQUENCY.calc(data.commits.last(1).years().by().weeks().average())
        score += CriticalityScorePolicy.CriticalityCheck.RECENT_RELEASES_COUNT.calc(data.releases.last(1).years().sum())
        return (score / weightsTotal).round(4)
    }
}

data class CriticalityData(
    val openedIssuesIn90Days: Int = 0,
    val closedIssuesIn90Days: Int = 0,
    val contributorsCount: Int = 0,
    val createdDate: LocalDate = LocalDate.now(),
    val lastActivityDate: LocalDate = LocalDate.now(),
    val commits: Timeline = Timeline(),
    val releases: Timeline = Timeline(),
) {

    fun toFeatures(): Features = Features(
        lastActivityFeature(), createdDateFeature(), commitsFeature(),
        issuesFeature(), releasesFeature(), contributorsFeature()
    )

    fun releasesFeature(): ReleasesFeature = ReleasesFeature(releases)
    fun contributorsFeature(): ContributorsFeature = ContributorsFeature(ContributorGenerator.generate(contributorsCount))
    fun lastActivityFeature(): LastActivityDateFeature = LastActivityDateFeature(lastActivityDate)
    fun createdDateFeature(): CreatedDateFeature = CreatedDateFeature(createdDate)
    fun commitsFeature(): CommitsFeature = CommitsFeature(commits)
    fun issuesFeature(): IssuesFeature = IssuesFeature(Issues(0, 0, 0, closedIssuesIn90Days, openedIssuesIn90Days))

}