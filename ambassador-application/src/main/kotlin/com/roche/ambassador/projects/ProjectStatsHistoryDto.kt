package com.roche.ambassador.projects

import com.roche.ambassador.storage.project.ProjectStatistics
import com.roche.ambassador.storage.project.ProjectStatisticsHistory
import java.time.LocalDate

internal data class ProjectStatsHistoryDto(
    val projectId: Long,
    val stats: Map<LocalDate, ProjectStatistics>
) {
    companion object {
        fun from(id: Long, statsHistory: List<ProjectStatisticsHistory>): ProjectStatsHistoryDto {
            val stats = statsHistory.associate { it.date.toLocalDate() to it.stats }
            return ProjectStatsHistoryDto(id, stats)
        }
    }
}