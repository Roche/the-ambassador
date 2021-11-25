package com.roche.ambassador.indexing.project

import com.roche.ambassador.model.source.CriterionVerifier
import com.roche.ambassador.indexing.IndexingCriterion
import java.util.function.Supplier

internal class CriteriaBuilder {
    private val criteria: MutableSet<IndexingCriterion> = mutableSetOf()

    fun add(name: String, verifier: CriterionVerifier): CriteriaBuilder {
        criteria.add(IndexingCriterion(name, verifier))
        return this
    }

    fun addIf(name: String, verifier: CriterionVerifier, condition: Supplier<Boolean>): CriteriaBuilder {
        return addIf(name, verifier, condition.get())
    }

    fun addIf(name: String, verifier: CriterionVerifier, flag: Boolean): CriteriaBuilder {
        if (flag) {
            return add(name, verifier)
        }
        return this
    }

    fun build(): IndexingCriteria {
        return IndexingCriteria(*criteria.toTypedArray())
    }
}
