package com.roche.ambassador.storage.project

import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.project.Permissions
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.stats.Statistics
import java.time.LocalDate

class ProjectEntityTest {

    private fun createProject(): Project = Project(
        1, "x",
        "x", "x",
        "x", "description", listOf(),
        Visibility.PRIVATE, "x",
        false, false, false,
        Statistics(), LocalDate.now(), LocalDate.now(),
        Permissions(true, true)
    )

//    @Test
//    fun `should create new stats history entry when recording stats`() {
//        val project = ProjectEntity(project = createProject(), lastIndexedDate = LocalDateTime.now(),)
//
//        val history = project.recordStatistics()
//
//        assertThat(project.statsHistory).hasSize(1)
//            .containsExactly(history)
//        assertThat(history)
//            .extracting(ProjectStatisticsHistory::date, ProjectStatisticsHistory::project)
//            .containsExactly(project.lastIndexedDate, project)
//    }
}
