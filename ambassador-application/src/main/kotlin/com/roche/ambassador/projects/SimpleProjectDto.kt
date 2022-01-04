package com.roche.ambassador.projects

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.project.Project
import java.time.LocalDate

@JsonPropertyOrder("id", "name", "description", "url", "tags", "score")
data class SimpleProjectDto(
    val id: Long,
    val url: String,
    val avatarUrl: String?,
    val name: String,
    val description: String?,
    val tags: List<String>?,
    val visibility: Visibility,
    val createdDate: LocalDate?,
    val lastUpdatedDate: LocalDate?,
    val mainLanguage: String?,
    val criticalityScore: Double,
    val activityScore: Int,
    val stars: Int,
    val score: Double
) {

    companion object {
        fun from(project: Project): SimpleProjectDto {
            return SimpleProjectDto(
                id = project.id,
                url = project.url!!,
                avatarUrl = project.avatarUrl,
                name = project.name,
                description = project.description,
                tags = project.tags,
                visibility = project.visibility,
                createdDate = project.createdDate,
                lastUpdatedDate = project.lastActivityDate,
                mainLanguage = project.getMainLanguage(),
                criticalityScore = project.getScores().criticality,
                activityScore = project.getScores().activity.toInt(),
                stars = project.stats.stars ?: 0,
                score = project.getScores().total
            )
        }
    }
}
