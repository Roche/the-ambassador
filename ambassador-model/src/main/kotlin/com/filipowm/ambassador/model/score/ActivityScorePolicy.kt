package com.filipowm.ambassador.model.score

import com.filipowm.ambassador.model.project.Project
import com.filipowm.ambassador.model.project.Visibility
import kotlin.math.log
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

object ActivityScorePolicy : ScorePolicy<Double> {

    private const val INITIAL_SCORE = 50.0

    override fun calculateScoreOf(project: Project): Double {


        var score = INITIAL_SCORE
        // weighting: forks and stars count
        score += project.stats.forks * 5 + project.stats.stars * 5
        // add some little score for open issues, too
//        score += withNotNull(project.issues) { it.open.toDouble() / 5 }
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
        score += if (project.description != null && project.description.length > 30) 50 else 0
        // give projects with contribution guidelines (CONTRIBUTING.md) file a static boost of 100
//        score += if (project.files.contributingGuide.exists) 100 else 0
        // give projects with readme (README.md) file a static boost of 100
//        score += if (project.files.readme.exists && project.files.readme.contentLength!! > 100) 100 else 0
        // give projects with license (LICENSE) file a static boost of 10
//        score += if (project.files.license.exists) 10 else 0
        // give projects with changelog (CHANGELOG.md) file a static boost of 50
//        score += if (project.files.changelog.exists) 50 else 0
        // evaluate participation stats for the previous 3 months
//        if (project.commits != null) {
////            // average commits: adds a bonus multiplier between 0..1 to overall score (1 = >10 commits per week, 0 = less than 3 commits per week)
//            val avg = project.commits.by().weeks().average()
//            score *= ((1 + min(max(avg - 3, .0), 7.0)) / 7)
//            //            let iAverageCommitsPerWeek = repo._opensourceMetadata.participation.slice(repo._opensourceMetadata.participation - 13).reduce((a, b) => a + b) / 13;
//        }

        // give penalty for private projects
        score *= if (project.visibility == Visibility.PRIVATE) .3f else 1f
        // build in a logarithmic scale for very active projects (open ended but stabilizing around 5000)
        if (score > 3000) {
            score = 3000 + log(score, 10.0) * 100
        }
        // final score is a rounded value starting from 0 (subtract the initial value)
        return max(round(score - INITIAL_SCORE), 0.0)
    }
}
