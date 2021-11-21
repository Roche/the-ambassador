package com.roche.ambassador.storage.project

import com.roche.ambassador.storage.PersistenceTest
import com.roche.ambassador.storage.utils.QueryAssertions.Companion.assertQueryCount
import com.roche.ambassador.storage.utils.hasInserted
import com.roche.ambassador.storage.utils.hasSelected
import com.roche.ambassador.storage.utils.hasUpdated
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@PersistenceTest
class ProjectRepositoryTest(@Autowired private val projectEntityRepository: ProjectEntityRepository) {

    @AfterEach
    fun cleanupRepositoryAfter() {
        projectEntityRepository.deleteAll()
    }

    @Test
    fun `should be able to save and read project`() {
        // given
        val project = ProjectCreator.create()
        val id = project.id
        val entity = ProjectEntity.from(project)

        // when
        val saved = projectEntityRepository.save(entity)

        // then
        assertThat(saved).isNotNull
        assertThat(saved.id).isEqualTo(id)
        assertThat(saved.project.features.size).isGreaterThanOrEqualTo(5)
        assertThat(saved.project.scorecard!!.value).isGreaterThan(0.1)
        assertQueryCount()
            .hasInserted(1)
            .hasSelected(1)

        // when
        val read = projectEntityRepository.findById(id)

        // then
        assertThat(read).isPresent
            .get()
            .isEqualTo(saved)
        assertQueryCount().hasSelected(2)
    }

    @Test
    fun `should update existing project`() {
        // given
        val project = ProjectCreator.create()
        val id = project.id
        val entity = ProjectEntity.from(project)
        val saved = projectEntityRepository.save(entity)
        val updatedProject = ProjectCreator.create()
        val toUpdate = ProjectEntity.from(updatedProject)
        toUpdate.id = id

        // when
        val updated = projectEntityRepository.save(toUpdate)

        // then
        assertThat(updated.id).isEqualTo(saved.id)
        assertThat(updated).isNotEqualTo(saved)
        assertQueryCount().hasUpdated(1)
    }

}