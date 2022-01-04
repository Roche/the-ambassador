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
        val textSearchQuery: TextQueryHolder? = query.query
            .map { TextQueryHolder(it) }
            .orElse(null)
        val rankField = textSearchQuery?.asRank() ?: defaultScoreColumn().`as`(RANK_FIELD)
        val q = dsl.select(idColumn(), nameColumn(), dataColumn(), rankField).from(table())
        buildQuery(query, textSearchQuery, q)

        if (pageable.sort.isSorted) {
            q.orderBy(Sorting.within(table()).by(pageable.sort))
        } else {
            q.orderBy(Sorting.within(table()).by(rankField, Sort.Direction.DESC))
        }
        q.limit(pageable.pageSize)
        q.offset(pageable.offset)

        val records = q.fetch(mapper())
        val c = count(query, textSearchQuery).toLong()
        return PageImpl(records, pageable, c)
    }

    protected abstract fun table(): Table<*>
    protected abstract fun idColumn(): TableField<*, ID>
    protected abstract fun nameColumn(): TableField<*, String>
    protected abstract fun dataColumn(): TableField<*, *>
    protected abstract fun textsearchColumn(): TableField<*, *>
    protected abstract fun defaultScoreColumn(): TableField<*, *>
    protected abstract fun mapper(): RecordMapper<Record4<ID, String, *, *>, T>
    protected open fun additionalSearchCriteria(whereBuilder: SelectConditionStep<*>, searchQuery: Q) {
        // empty
    }

    private fun count(query: Q, textQueryHolder: TextQueryHolder?): Int {
        val select = dsl.select(DSL.count()).from(table())
        buildQuery(query, textQueryHolder, select)
        return select.fetch { it.value1() }.first()
    }

    private fun buildQuery(query: Q, textQueryHolder: TextQueryHolder?, q: SelectWhereStep<*>) {
        val whereBuilder = q.where()
        if (textQueryHolder != null) {
            applyFullTextSearch(whereBuilder, textQueryHolder)
        }
        additionalSearchCriteria(whereBuilder, query)
    }

    private fun applyFullTextSearch(q: SelectConditionStep<out Record>, query: TextQueryHolder) {
        q.and(query.asQuery())
    }
    private inner class TextQueryHolder(query: String) {

        private val function: SearchFunction
        private val query: Param<String>
        private val lang: Param<String> = DSL.inline(language)

        init {
            val trimmed = query.trim()
            if (trimmed.split(" ").size > 1) {
                this.function = SearchFunction.WEBSEARCH
                this.query = DSL.inline(trimmed)
            } else {
                this.function = SearchFunction.SIMPLE
                this.query = DSL.inline("$trimmed:*")
            }
        }

        fun asQuery(): Field<Boolean> {
            val fullQuery = "{0} @@ ${function.function}"
            return DSL.field(fullQuery, Boolean::class.java, textsearchColumn(), lang, query)
        }

        fun asRank(): Field<Double> {
            val fullField = "ts_rank_cd({0}, ${function.function}) * {3}"
            return DSL.field(fullField, Double::class.java, textsearchColumn(), lang, query, defaultScoreColumn()).`as`(RANK_FIELD)
        }
    }

    private enum class SearchFunction(val function: String) {
        WEBSEARCH("websearch_to_tsquery({1}, {2})"),
        SIMPLE("to_tsquery({1}, {2})")
    }
}
