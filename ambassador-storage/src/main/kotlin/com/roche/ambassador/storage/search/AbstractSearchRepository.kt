package com.roche.ambassador.storage.search

import com.roche.ambassador.storage.jooq.Sorting
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

abstract class AbstractSearchRepository<T, Q : SearchQuery, ID>(
    private val dsl: DSLContext,
    @Value("\${ambassador.language}")
    private val language: String
) : SearchRepository<T, Q> {

    companion object {
        const val RANK_FIELD = "search_rank"
    }

    override fun search(query: Q, pageable: Pageable): Page<T> {
        val rankField = if (pageable.sort.isSorted || query.query.isEmpty) {
            defaultScoreColumn().`as`(RANK_FIELD)
        } else {
            rank(textsearchColumn(), query.query.get())
        }
        val q = dsl.select(idColumn(), nameColumn(), dataColumn(), rankField).from(table())
        buildQuery(query, q)

        if (pageable.sort.isSorted) {
            q.orderBy(Sorting.within(table()).by(pageable.sort))
        } else {
            q.orderBy(Sorting.within(table()).by(rankField, Sort.Direction.DESC))
        }
        q.limit(pageable.pageSize)
        q.offset(pageable.offset)

        val records = q.fetch(mapper())
        val c = count(query).toLong()
        return PageImpl(records, pageable, c)
    }

    protected abstract fun table(): Table<*>
    protected abstract fun idColumn(): TableField<*, ID>
    protected abstract fun nameColumn(): TableField<*, String>
    protected abstract fun dataColumn(): TableField<*, *>
    protected abstract fun textsearchColumn(): TableField<*, *>
    protected abstract fun defaultScoreColumn(): TableField<*, *>
    protected abstract fun mapper(): RecordMapper<Record4<ID, String, *, *>, T>


    private fun count(query: Q): Int {
        val s = dsl.select(DSL.count()).from(table())
        buildQuery(query, s)
        return s.fetch { it.value1() }.first()
    }

    private fun rank(field: TableField<*, *>, query: String): Field<Double> {
        return DSL.field("ts_rank_cd({0}, to_tsquery({1}, {2})) * {3}",
                         Double::class.java, field, DSL.inline(language), DSL.inline("$query:*"), defaultScoreColumn()).`as`(RANK_FIELD)
    }

    private fun buildQuery(query: Q, q: SelectWhereStep<*>) {
        val whereBuilder = q.where()
        query.query.ifPresent { applyFullTextSearch(whereBuilder, it) }

        additionalSearchCriteria(whereBuilder, query)
    }

    private fun applyFullTextSearch(q: SelectConditionStep<out Record>, query: String) {
        q.and(textsearch(textsearchColumn(), query))
    }

    private fun textsearch(field: TableField<*, *>, query: String): Field<Boolean> {
        return DSL.field("{0} @@ to_tsquery({1}, {2})", Boolean::class.java, field, DSL.inline(language), DSL.inline("$query:*"))
    }

    protected open fun additionalSearchCriteria(whereBuilder: SelectConditionStep<*>, searchQuery: Q) {
        // empty
    }

}