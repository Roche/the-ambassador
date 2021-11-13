package com.roche.ambassador.project.indexer.internals

import com.roche.ambassador.model.source.CriterionVerifier
import com.roche.ambassador.model.source.ProjectDetailsResolver
import com.roche.ambassador.project.indexer.IndexingCriterion
import java.util.function.Supplier

internal class CriteriaBuilder<T>(private val projectDetailsResolver: ProjectDetailsResolver<T>) {
    private val criteria: MutableSet<IndexingCriterion<T>> = mutableSetOf()

    fun add(name: String, verifier: CriterionVerifier<T>): CriteriaBuilder<T> {
        criteria.add(IndexingCriterion(name, verifier, projectDetailsResolver))
        return this
    }

    fun addIf(name: String, verifier: CriterionVerifier<T>, condition: Supplier<Boolean>): CriteriaBuilder<T> {
        return addIf(name, verifier, condition.get())
    }

    fun addIf(name: String, verifier: CriterionVerifier<T>, flag: Boolean): CriteriaBuilder<T> {
        if (flag) {
            return add(name, verifier)
        }
        return this
    }

    fun build(): IndexingCriteria<T> {
        return IndexingCriteria(*criteria.toTypedArray())
    }
}
