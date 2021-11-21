package com.roche.ambassador.storage.project

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
import java.util.concurrent.TimeUnit

/**
 * !!!!! READ ME !!!!!
 * Please minimize number of test methods here
 * and reuse existing test methods whenever possible,
 * because tests here are quite long running due to a number of data to insert and query
 */
@PersistenceTest
@Timeout(5, unit = TimeUnit.SECONDS)
@Import(JooqConfiguration::class, SearchableProjectEntityRepository::class)
@Sql("/sql/projects_for_search.sql")
class SearchableProjectRepositoryTest(
    @Autowired private val entityRepository: ProjectEntityRepository,
    @Autowired private val repository: SearchableProjectEntityRepository
) {

    @AfterEach
    fun cleanup() {
        entityRepository.deleteAll()
    }

    @Test
    fun `should find all expected results`() {
        // when
        val result = repository.search(SearchQuery.of("accio"), PageRequest.of(0, 15))

        // then
        assertThat(result).hasSize(15)
        assertThat(result.totalElements).isEqualTo(38)

        // when
        val result2 = repository.search(SearchQuery.of("acc"), PageRequest.of(0, 15))
        assertThat(result2).hasSize(15)
        assertThat(result2.totalElements).isEqualTo(380)

        // when
        val result3 = repository.search(SearchQuery.of("accioaccio"), PageRequest.of(0, 15))
        assertThat(result3).hasSize(0)
        assertThat(result3.totalElements).isEqualTo(0)
    }

    @Test
    fun `should use basic word of form and find results`() {
        // when
        val result = repository.search(SearchQuery.of("EXPEDITIONS"), PageRequest.of(0, 15))

        // then
        assertThat(result).hasSize(15)
        assertThat(result.totalElements).isEqualTo(226)
        assertOnlySelectAndCountQueries()
    }

    @Test
    fun `should find all sorted by score if no query provided`() {
        // when
        val result = repository.search(SearchQuery.of(), PageRequest.of(0, 30))

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
        val result = repository.search(SearchQuery.of("accio"), PageRequest.of(0, 30, Sort.by("name")))

        // then
        assertThat(result.content)
            .hasSize(30)
            .isSortedAccordingTo { projectEntity, projectEntity2 -> projectEntity.name!!.compareTo(projectEntity2.name!!) }
        assertOnlySelectAndCountQueries()
    }

    private fun ProjectEntity.extractScore(): Double {
        return project?.scorecard?.value ?: 0.0
    }

    private fun assertOnlySelectAndCountQueries(){
        // two queries -- one for actual select, and one for calculating total amount of data
        assertQueryCount()
            .hasSelected(2)
            .hasSuccessful(2)
    }
}