package com.roche.ambassador.project

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.roche.ambassador.model.project.Visibility
import com.roche.ambassador.storage.project.ProjectHistoryEntity
import java.time.LocalDate
import java.time.LocalDateTime

@JsonPropertyOrder("id", "name", "indexedDate", "score")
data class ProjectHistoryDto(
    val url: String?,
    val name: String,
    val description: String?,
    val tags: List<String>?,
    val visibility: Visibility,
    val lastUpdatedDate: LocalDate?,
    val mainLanguage: String?,
    val criticalityScore: Double,
    val activityScore: Double,
    val stars: Int,
    val score: Double,
    val indexedDate: LocalDateTime
) {
    companion object {
        fun from(projectHistoryEntity: ProjectHistoryEntity): ProjectHistoryDto {
            val project = projectHistoryEntity.project!!
            return ProjectHistoryDto(
                project.url, project.name,
                project.description, project.tags, project.visibility,
                project.lastActivityDate, project.getMainLanguage(),
                project.getScores().criticality, project.getScores().activity,
                project.stats.stars, project.getScores().total, projectHistoryEntity.indexedDate
            )
        }
    }
}
