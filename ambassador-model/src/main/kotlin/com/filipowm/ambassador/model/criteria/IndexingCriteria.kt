package com.filipowm.ambassador.model.criteria

import org.slf4j.LoggerFactory

open class IndexingCriteria<T>(vararg criteria: IndexingCriterion<T>): Criteria<T> {

    private val criteria = criteria.toList()

    companion object {
        private val log = LoggerFactory.getLogger(IndexingCriteria::class.java)

        fun <T> none(): IndexingCriteria<T> {
            return object : IndexingCriteria<T>() {
                override fun evaluate(input: T): Boolean = true
            }
        }
    }

    override fun evaluate(input: T): Boolean {
        for (criterion in criteria) {

            if (!criterion.test(input)) {
                log.warn(criterion.getFailureMessage(input))
                criterion.onCriterionFailure(input)
                return false
            }
        }
        return true
    }

}