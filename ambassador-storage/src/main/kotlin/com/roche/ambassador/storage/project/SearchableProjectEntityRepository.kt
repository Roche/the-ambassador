package com.roche.ambassador.storage.project

import com.fasterxml.jackson.databind.ObjectMapper
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.project.Visibility
import com.roche.ambassador.storage.jooq.Json
import com.roche.ambassador.storage.jooq.Sorting
import com.roche.ambassador.storage.jooq.tables.Project.PROJECT
import com.roche.ambassador.storage.jooq.tables.records.ProjectRecord
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class SearchableProjectEntityRepository(
    private val dsl: DSLContext,
    private val objectMapper: ObjectMapper,
    @Value("\${ambassador.language}")
    private val language: String
) : ProjectSearchRepository {

    private val mapper = RecordMapper<Record4<Long, String, JSONB, *>, ProjectEntity> {
        ProjectEntity(
            it.get(PROJECT.ID),
            it.get(PROJECT.NAME),
            objectMapper.readValue(it.get(PROJECT.PROJECT_).data(), Project::class.java),
        )
    }

    companion object {
        const val RANK_FIELD = "search_rank"
    }

    private fun count(query: SearchQuery): Int {
        val s = dsl.select(DSL.count()).from(PROJECT)
        buildQuery(query, s)
        return s.fetch { it.value1() }.first()
    }

    private fun rank(field: TableField<ProjectRecord, Any>, query: String): Field<Double> {
        return DSL.field("ts_rank_cd({0}, to_tsquery({1}, {2})) * {3}",
                         Double::class.java, field, DSL.inline(language), DSL.inline("$query:*"), PROJECT.SCORE).`as`(RANK_FIELD)
    }

    override fun search(query: SearchQuery, pageable: Pageable): Page<ProjectEntity> {
        val rankField = if (pageable.sort.isSorted || query.query.isEmpty) {
            PROJECT.SCORE.`as`(RANK_FIELD)
        } else {
            rank(PROJECT.TEXTSEARCH, query.query.get())
        }
        val q = dsl.select(PROJECT.ID, PROJECT.NAME, PROJECT.PROJECT_, rankField).from(PROJECT)
        buildQuery(query, q)

        if (pageable.sort.isSorted) {
            q.orderBy(Sorting.within(PROJECT).by(pageable.sort))
        } else {
            q.orderBy(Sorting.within(PROJECT).by(rankField, Sort.Direction.DESC))
        }
        q.limit(pageable.pageSize)
        q.offset(pageable.offset)

        val records = q.fetch(mapper)
        val c = count(query).toLong()
        return PageImpl(records, pageable, c)
    }

    private fun buildQuery(query: SearchQuery, q: SelectWhereStep<*>) {
        val whereBuilder = q.where()
        query.query.ifPresent { applyFullTextSearch(whereBuilder, it) }

        searchWithinJson(whereBuilder, query)
    }

    private fun applyFullTextSearch(q: SelectConditionStep<out Record>, query: String) {
        q.and(textsearch(PROJECT.TEXTSEARCH, query))
    }

    private fun textsearch(field: TableField<ProjectRecord, Any>, query: String): Field<Boolean> {
        return DSL.field("{0} @@ to_tsquery({1}, {2})", Boolean::class.java, field, DSL.inline(language), DSL.inline("$query:*"))
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
}
