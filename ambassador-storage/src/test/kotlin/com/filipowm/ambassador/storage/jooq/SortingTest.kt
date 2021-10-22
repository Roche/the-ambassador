package com.filipowm.ambassador.storage.jooq

import com.filipowm.ambassador.storage.InvalidSortFieldException
import com.filipowm.ambassador.storage.jooq.tables.Project
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort

@DisplayName("jOOQ sorting helper test")
class SortingTest {

    @Test
    fun `should create sort fields based on sort specification`() {
        // given sort specification with two fields
        val spec = Sort.by(Sort.Direction.DESC, "score")
            .and(Sort.by(Sort.Direction.ASC, "name"))

        // when create sorting from spec
        val actual = Sorting.within(Project.PROJECT).by(spec)

        // then
        assertThat(actual)
            .hasSize(2)
            .containsExactly(Project.PROJECT.SCORE.desc(), Project.PROJECT.NAME.asc())
    }

    @Test
    fun `should create sort field for single field`() {
        // when sorting by single field
        val actual = Sorting.within(Project.PROJECT).by("name", Sort.Direction.DESC)

        // then
        assertThat(actual).hasSize(1)
            .containsExactly(Project.PROJECT.NAME.desc())
    }

    @Test
    fun `should throw exception when field does not exist`() {
        assertThatThrownBy { Sorting.within(Project.PROJECT).by("__invalid__", Sort.Direction.ASC) }
            .isInstanceOf(InvalidSortFieldException::class.java)
            .hasFieldOrPropertyWithValue("field", "__invalid__")
    }
}