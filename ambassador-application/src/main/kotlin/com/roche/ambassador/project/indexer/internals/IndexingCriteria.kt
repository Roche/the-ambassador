package com.roche.ambassador.project.indexer.internals

import com.roche.ambassador.model.source.IndexingCriteriaProvider
import com.roche.ambassador.model.source.ProjectDetailsResolver
import com.roche.ambassador.project.indexer.IndexingCriterion

internal open class IndexingCriteria<T>(vararg criteria: IndexingCriterion<T>) {

    private val criteria = criteria.toList()

    companion object {

        fun <T> none(): IndexingCriteria<T> {
            return object : IndexingCriteria<T>() {
                override fun evaluate(input: T): CriteriaEvaluationResult<T> = CriteriaEvaluationResult(listOf())
            }
        }

        fun forProvider(
            projectDetailsResolver: ProjectDetailsResolver<Any>,
            criteriaProvider: IndexingCriteriaProvider<Any>
        ): IndexingCriteria<Any> {
            return CriteriaBuilder(projectDetailsResolver)
                .createCriteriaFrom(criteriaProvider.getInvalidProjectCriteria())
                .addCriteria("excludeAllForks", criteriaProvider.getForkedProjectCriteria().excludeAllWithForks())
//                .addCriteria("personalProjectWithAtLeast1Star", criteriaProvider.getPersonalProjectCriteria().hasAtLeastStars(1))
                .build()
        }
    }

    open fun evaluate(input: T): CriteriaEvaluationResult<T> {
        val failedCriteria = mutableListOf<IndexingCriterion<T>>()
        for (criterion in criteria) {
            if (!criterion.test(input)) {
                failedCriteria.add(criterion)
            }
        }
        return CriteriaEvaluationResult(failedCriteria)
    }

    fun getAllCriteriaNames() = criteria.joinToString(",") { it.name }

    data class CriteriaEvaluationResult<T> internal constructor(
        val failedCriteria: List<IndexingCriterion<T>> = listOf()
    ) {
        val success = failedCriteria.isEmpty()
        val failure = failedCriteria.isNotEmpty()

        fun whenFailed(block: (List<IndexingCriterion<T>>) -> Unit) {
            if (!success) {
                block.invoke(failedCriteria)
            }
        }
    }
}
