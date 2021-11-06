package com.roche.ambassador.storage.project

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ProjectEntityTest {

    @Test
    fun `should create new history entry when creating a snapshot`() {
        val project = ProjectEntity(lastIndexedDate = LocalDateTime.now())

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
        val project = ProjectEntity(lastIndexedDate = LocalDateTime.now().minusDays(7))
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
