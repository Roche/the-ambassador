package com.roche.ambassador.storage.project

import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.stats.Statistics
import com.roche.ambassador.storage.PersistenceTest
import com.roche.ambassador.storage.jooq.JooqConfiguration
import com.roche.ambassador.storage.utils.QueryAssertions.Companion.assertQueryCount
import com.roche.ambassador.storage.utils.hasSelected
import com.roche.ambassador.storage.utils.hasSuccessful
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/**
 * !!!!! READ ME !!!!!
 * Please minimize number of test methods here
 * and reuse existing test methods whenever possible,
 * because tests here are quite long running due to a number of data to insert and query
 */
@PersistenceTest
//@Timeout(10, unit = TimeUnit.SECONDS)
@Import(JooqConfiguration::class, ProjectSearchRepository::class)
@Sql("/sql/projects_for_search.sql")
class ProjectSearchRepositoryTest(
    @Autowired private val entityRepository: ProjectEntityRepository,
    @Autowired private val repository: ProjectSearchRepository
) {

    @AfterEach
    fun cleanup() {
        entityRepository.deleteAll()
    }

    @Test
    fun `should find all expected results for single-word query`() {
        // when
        val result = repository.search(ProjectSearchQuery.of("accio"), PageRequest.of(0, 15))

        // then
        assertThat(result).hasSize(15)
        assertThat(result.totalElements).isEqualTo(29)

        // when
        val result2 = repository.search(ProjectSearchQuery.of("acc"), PageRequest.of(0, 15))
        assertThat(result2).hasSize(15)
        assertThat(result2.totalElements).isEqualTo(366)

        // when
        val result3 = repository.search(ProjectSearchQuery.of("accioaccio"), PageRequest.of(0, 15))
        assertThat(result3).hasSize(0)
        assertThat(result3.totalElements).isEqualTo(0)
    }

    @Test
    fun `should find all expected results for sentence query`() {
        // when
        val result = repository.search(ProjectSearchQuery.of("error possimus"), PageRequest.of(0, 15))

        // then
        assertThat(result.totalElements).isEqualTo(86)

        // when
        val result2 = repository.search(ProjectSearchQuery.of("voluptas ipsum"), PageRequest.of(0, 15))
        assertThat(result2.totalElements).isEqualTo(200)
    }

    @Test
    fun `should use basic word of form and find results for single-word query`() {
        // when
        val result = repository.search(ProjectSearchQuery.of("EXPEDITIONS"), PageRequest.of(0, 15))

        // then
        assertThat(result).hasSize(15)
        assertThat(result.totalElements).isEqualTo(243)
        assertOnlySelectAndCountQueries()
    }

    @Test
    fun `should find all sorted by score if no query provided`() {
        // when
        val result = repository.search(ProjectSearchQuery.of(), PageRequest.of(0, 30))

        // then
        assertThat(result.totalElements).isEqualTo(500)
        assertThat(result.content)
            .hasSize(30)
            .isSortedAccordingTo { projectEntity, projectEntity2 -> projectEntity2.extractScore().compareTo(projectEntity.extractScore()) }
        assertOnlySelectAndCountQueries()
    }

    @Test
    fun `should sort by defined field`() {
        // when
        val result = repository.search(ProjectSearchQuery.of("accio"), PageRequest.of(0, 15, Sort.by("name")))

        // then
        assertThat(result.content)
            .hasSize(15)
            .isSortedAccordingTo { projectEntity, projectEntity2 -> projectEntity.name!!.compareTo(projectEntity2.name!!) }
        assertOnlySelectAndCountQueries()
    }

    @Test
    fun `should recalculate textsearch index on row update`() {
        // given
        val name = "beautiful pangolier"
        val project = createProject(name)
        val projectEntity = ProjectEntity(project.id,  name, project)
        val created = entityRepository.save(projectEntity)

        // when
        val result = repository.search(ProjectSearchQuery.of(name), PageRequest.of(0, 15, Sort.by("name")))

        // then
        assertThat(result.content).hasSize(1)

        // when
        val nameUpdated = "fluffy squirrel"
        created.name = nameUpdated
        entityRepository.save(created)

        // when
        val resultUpdatedOldName = repository.search(ProjectSearchQuery.of(name), PageRequest.of(0, 15, Sort.by("name")))

        // then
        assertThat(resultUpdatedOldName.content).isEmpty()

        // when
        val resultUpdatedNewName = repository.search(ProjectSearchQuery.of(nameUpdated), PageRequest.of(0, 15, Sort.by("name")))

        // then
        assertThat(resultUpdatedNewName.content).hasSize(1)
    }

    private fun createProject(name: String) : Project {
        return Project(
            999999, "http://somerandomurlthatnoonehopefullywillverify.com", "http://yetanotherrandomurlthatnoonehopefullywillverify.com",
            name, "test/$name", null, listOf(), Visibility.INTERNAL, "main",
            false, false, false, Statistics(), LocalDate.now(), LocalDate.now(), null
        )
    }

    private fun ProjectEntity.extractScore(): Double {
        return project.scorecard?.value ?: 0.0
    }

    private fun assertOnlySelectAndCountQueries(){
        // two queries -- one for actual select, and one for calculating total amount of data
        assertQueryCount()
            .hasSelected(2)
            .hasSuccessful(2)
    }
}