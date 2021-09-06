package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.model.criteria.Criteria
import com.filipowm.ambassador.model.source.IndexingCriteriaProvider
import com.filipowm.ambassador.model.source.ProjectDetailsResolver
import org.slf4j.LoggerFactory

internal open class IndexingCriteria<T>(vararg criteria: IndexingCriterion<T>) : Criteria<T> {

    private val criteria = criteria.toList()

    companion object {
        private val log = LoggerFactory.getLogger(IndexingCriteria::class.java)

        fun <T> none(): IndexingCriteria<T> {
            return object : IndexingCriteria<T>() {
                override fun evaluate(input: T): Boolean = true
            }
        }

        fun forProvider(projectDetailsResolver: ProjectDetailsResolver<Any>,criteriaProvider: IndexingCriteriaProvider<Any>): IndexingCriteria<Any> {
            return CriteriaBuilder(projectDetailsResolver)
                .createCriteriaFrom(criteriaProvider.getInvalidProjectCriteria())
                .build()
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

    fun getAllCriteriaNames() = criteria.joinToString(",") { it.name }

}