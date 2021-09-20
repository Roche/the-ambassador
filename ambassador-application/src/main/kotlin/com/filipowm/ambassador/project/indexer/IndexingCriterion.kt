package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.model.criteria.Criterion
import com.filipowm.ambassador.model.source.CriterionVerifier
import com.filipowm.ambassador.model.source.ProjectDetailsResolver

class IndexingCriterion<T>(
    val name: String,
    private val criteriaVerifier: CriterionVerifier<T>,
    private val projectDetailsResolver: ProjectDetailsResolver<T>,
    private val failureMessageSupplier: ((T) -> String)? = null
) : Criterion<T> {

    override fun getFailureMessage(input: T): String = failureMessageSupplier?.invoke(input) ?: "Evaluation of ${this.name} criteria failed on project '${
        projectDetailsResolver.resolveName(
            input
        )
    }' (id=${projectDetailsResolver.resolveId(input)})"

    override fun evaluate(input: T): Boolean = criteriaVerifier(input)

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean = if (other is IndexingCriterion<*>) {
        this.name == other.name
    } else {
        false
    }

    override fun hashCode(): Int = name.hashCode()
}
