package com.roche.ambassador.storage.project

import com.roche.ambassador.model.project.Project
import java.time.LocalDate

class ProjectStatistics(
    val totalScore: Double,
    val activity: Double,
    val criticality: Double,
    val lastActivityDate: LocalDate? = null,
    val forks: Int? = null,
    val stars: Int? = null,
    val commits: Long? = null
) {

    companion object Factory {
        fun from(project: Project): ProjectStatistics {
            return ProjectStatistics(
                totalScore = project.getScores().total,
                activity = project.getScores().activity,
                criticality = project.getScores().criticality,
                lastActivityDate = project.lastActivityDate,
                forks = project.stats.forks,
                stars = project.stats.stars,
                commits = project.stats.commits
            )
        }
    }
}
