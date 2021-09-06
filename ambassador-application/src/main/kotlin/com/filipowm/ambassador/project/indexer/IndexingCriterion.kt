package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.model.criteria.Criterion
import com.filipowm.ambassador.model.source.CriterionVerifier
import com.filipowm.ambassador.model.source.ProjectDetailsResolver

internal class IndexingCriterion<T>(
    val name: String,
    private val criteriaVerifier: CriterionVerifier<T>,
    private val projectDetailsResolver: ProjectDetailsResolver<T>,
    private val failureMessageSupplier: ((T) -> String)? = null,
    private val onCriterionFailure: (T) -> Unit = {}
) : Criterion<T> {

    fun onCriterionFailure(input: T) = onCriterionFailure.invoke(input)

    override fun getFailureMessage(input: T) = failureMessageSupplier?.invoke(input) ?: "Evaluation of ${this.name} criteria failed on project '${projectDetailsResolver.resolveName(input)}' (id=${projectDetailsResolver.resolveId(input)})"

    override fun evaluate(input: T) = criteriaVerifier(input)

    override fun toString(): String = name

    override fun equals(other: Any?) = if (other is IndexingCriterion<*>) {
        this.name == other.name
    } else {
        false
    }

    override fun hashCode() = name.hashCode()
}