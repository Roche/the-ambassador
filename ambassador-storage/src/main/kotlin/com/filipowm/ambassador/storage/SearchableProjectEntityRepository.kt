package com.filipowm.ambassador.storage

import com.fasterxml.jackson.databind.ObjectMapper
import com.filipowm.ambassador.model.project.Project
import com.filipowm.ambassador.model.project.Visibility
import com.filipowm.ambassador.storage.jooq.Json
import com.filipowm.ambassador.storage.jooq.tables.Project.PROJECT
import com.filipowm.ambassador.storage.jooq.tables.records.ProjectRecord
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
internal class SearchableProjectEntityRepository(private val dsl: DSLContext, private val objectMapper: ObjectMapper) : ProjectSearchRepository {

    fun mapper(): RecordMapper<ProjectRecord, ProjectEntity> {
        return RecordMapper {
            ProjectEntity(
                it.id.toLong(),
                it.name,
                it.excerpt,
                objectMapper.readValue(it.project.data(), Project::class.java),
                it.stars,
                it.criticalityScore.toDouble(),
                it.activityScore.toDouble()
            )
        }
    }

    fun similarity(a: Field<String>, b: String): Field<Double> {
        return DSL.field("similarity({0}, {1})", Double::class.java, a, b)
    }

    private fun count(query: SearchQuery): Int {
        val s = dsl.select(DSL.count()).from(PROJECT)
        buildQuery(query, s)
        return s.fetch { it.value1() }.first()
    }

    override fun search(query: SearchQuery, pageable: Pageable): Page<ProjectEntity> {
        val q = dsl.selectFrom(PROJECT)
        buildQuery(query, q)

        q.orderBy(Sorting.within(PROJECT).by(pageable.sort))
        q.limit(pageable.pageSize)
        q.offset(pageable.offset)

        val record = q.fetch(mapper())
        val c = count(query).toLong()
        return PageImpl(record, pageable, c)
    }

    private fun buildQuery(query: SearchQuery, q: SelectWhereStep<*>) {
        val whereBuilder = q.where()
        query.query.ifPresent { applyFuzzySearch(whereBuilder, it) }

        searchWithinJson(whereBuilder, query)
    }

    private fun searchWithinJson(whereBuilder: SelectConditionStep<*>, searchQuery: SearchQuery) {
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

    private fun applyFuzzySearch(q: SelectConditionStep<*>, query: String) {
        q.and(
            similarity(PROJECT.NAME, query).gt(0.15)
                .or(similarity(PROJECT.EXCERPT, query).gt(0.15))
        )
    }
}
