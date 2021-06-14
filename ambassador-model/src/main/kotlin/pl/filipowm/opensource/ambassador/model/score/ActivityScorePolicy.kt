package pl.filipowm.opensource.ambassador.model.score

import pl.filipowm.opensource.ambassador.model.Project
import pl.filipowm.opensource.ambassador.model.Source
import pl.filipowm.opensource.ambassador.model.Visibility
import kotlin.math.log
import kotlin.math.min
import kotlin.math.round

object ActivityScorePolicy : ScorePolicy<Double> {
    override fun calculateScoreOf(project: Project) = Score.from(Source.BASICS) { calculate(project) }

    private fun calculate(project: Project): Double {
        var score = 50.0
        // weighting: forks and stars count
//        score += project.forksCount * 5 + project.starsCount / 3
        // add some little score for open issues, too
//        score += project.issues.openIssuesCount / 5
        val daysSinceLastUpdate = project.getDaysSinceLastUpdate()
        val daysSinceCreation = project.getDaysSinceCreation()
        // updated in last 3 months: adds a bonus multiplier between 0..1 to overall score (1 = updated today, 0 = updated more than 100 days ago)
        score *= (1 + (100 - min(daysSinceLastUpdate, 100).toDouble()) / 100)

        // all repositories updated in the previous year will receive a boost of maximum 1000 declining by days since last update
        var boost = (1000 - min(daysSinceLastUpdate, 365).toDouble() * 2.74)
        // gradually scale down boost according to repository creation date to mix with "real" engagement stats
        boost *= (365 - min(daysSinceCreation, 365).toDouble()) / 365
        // add boost to score
        score += boost
        // give projects with a meaningful description a static boost of 50
        score += if (project.description!!.length > 30) 50 else 0
        // give projects with contribution guidelines (CONTRIBUTING.md) file a static boost of 100
//        score += if (project.contributingGuide.exists) 100 else 0
        // give projects with contribution guidelines (CONTRIBUTING.md) file a static boost of 100
//        score += if (project.readme.exists && project.readme.length!! > 100) 50 else 0
        // give projects with contribution guidelines (CONTRIBUTING.md) file a static boost of 100
//        score += if (project.license.exists) 10 else 0
        // evaluate participation stats for the previous  3 months
//        repo._opensourceMetadata = repo._opensourceMetadata || {};
//        if (repo._opensourceMetadata.participation) {
//            // average commits: adds a bonus multiplier between 0..1 to overall score (1 = >10 commits per week, 0 = less than 3 commits per week)
//            let iAverageCommitsPerWeek = repo._opensourceMetadata.participation.slice(repo._opensourceMetadata.participation - 13).reduce((a, b) => a + b) / 13;
//            iScore = iScore * (1 + (Math.min(Math.max(iAverageCommitsPerWeek - 3, 0), 7)) / 7);
//        }

        // give penalty for private projects
        score *= if (project.visibility == Visibility.PRIVATE) .6f else 1f
        // build in a logarithmic scale for very active projects (open ended but stabilizing around 5000)
        if (score > 3000) {
            score = 3000 + log(score, 10.0) * 100;
        }
        // final score is a rounded value starting from 0 (subtract the initial value)
        return round(score - 50)
    }
}