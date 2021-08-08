package pl.filipowm.opensource.ambassador.project

import pl.filipowm.opensource.ambassador.model.Project
import pl.filipowm.opensource.ambassador.model.Visibility
import java.time.LocalDate

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
    val stars: Int
) {

    companion object {
        fun from(project: Project): SimpleProjectDto {
            return SimpleProjectDto(
                id = project.id,
                url = project.url,
                avatarUrl = project.avatarUrl,
                name = project.name,
                description = project.description,
                tags = project.tags,
                visibility = project.visibility,
                createdDate = project.createdDate,
                lastUpdatedDate = project.lastUpdatedDate,
                mainLanguage = project.getMainLanguage(),
                criticalityScore = project.getScores().criticality,
                activityScore = project.getScores().activity.toInt(),
                stars = project.stats.stars
            )
        }
    }
}