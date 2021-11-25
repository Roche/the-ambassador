package com.roche.ambassador.storage.project

import com.fasterxml.jackson.databind.ObjectMapper
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.storage.jooq.Json
import com.roche.ambassador.storage.jooq.tables.Project.PROJECT
import com.roche.ambassador.storage.search.AbstractSearchRepository
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class ProjectSearchRepository(
    dsl: DSLContext,
    private val objectMapper: ObjectMapper,
    @Value("\${ambassador.language}")
    language: String
) : AbstractSearchRepository<ProjectEntity, ProjectSearchQuery, Long>(dsl, language) {

    override fun additionalSearchCriteria(whereBuilder: SelectConditionStep<*>, searchQuery: ProjectSearchQuery) {
        whereBuilder.and(byVisibility(searchQuery.visibility))
    }

    private fun byVisibility(visibility: Visibility): Condition {
        val json = Json(PROJECT.PROJECT_)
        return visibility
            .getThisAndLessStrict()
            .stream()
            .map { it.name }
            .map { json.field("visibility", String::class.java).eq(it) }
            .reduce(DSL.falseCondition(), Condition::or)
    }

    override fun table(): Table<*> = PROJECT

    override fun idColumn(): TableField<*, Long> = PROJECT.ID

    override fun nameColumn(): TableField<*, String> = PROJECT.NAME

    override fun dataColumn(): TableField<*, *> = PROJECT.PROJECT_

    override fun textsearchColumn(): TableField<*, *> = PROJECT.TEXTSEARCH

    override fun defaultScoreColumn(): TableField<*, *> = PROJECT.SCORE

    override fun mapper(): RecordMapper<Record4<Long, String, *, *>, ProjectEntity> {
        return RecordMapper<Record4<Long, String, *, *>, ProjectEntity> {
            ProjectEntity(
                it.get(idColumn()),
                it.get(nameColumn()),
                objectMapper.readValue(it.get(PROJECT.PROJECT_).data(), Project::class.java),
            )
        }
    }
}
