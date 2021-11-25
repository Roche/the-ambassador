package com.roche.ambassador.storage.project

import com.roche.ambassador.model.project.Permissions
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.stats.Statistics
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

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

    @Test
    fun `should create new history entry when creating a snapshot`() {
        val project = ProjectEntity(lastIndexedDate = LocalDateTime.now(), project = createProject())

        val history = project.snapshot()

        assertThat(project.history).hasSize(1)
            .containsExactly(history)
        assertThat(history)
            .extracting(ProjectHistoryEntity::indexedDate, ProjectHistoryEntity::parent)
            .containsExactly(project.lastIndexedDate, project)
    }

    @Test
    fun `should remove old history when trying to match history size limit`() {
        // given project with 3 history items
        val project = ProjectEntity(lastIndexedDate = LocalDateTime.now().minusDays(7), project = createProject())
        val history1 = project.snapshot()
        project.lastIndexedDate = LocalDateTime.now().minusDays(5)
        val history2 = project.snapshot()
        project.lastIndexedDate = LocalDateTime.now().minusDays(3)
        val history3 = project.snapshot()
        assertThat(project.history)
            .hasSize(3)
            .containsExactly(history1, history2, history3)

        // when
        project.removeHistoryToMatchLimit(1)

        // then
        assertThat(project.history)
            .hasSize(1)
            .containsExactly(history3)
    }
}
