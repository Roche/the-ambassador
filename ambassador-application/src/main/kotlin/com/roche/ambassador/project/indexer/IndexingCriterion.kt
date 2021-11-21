package com.roche.ambassador.project.indexer

import com.roche.ambassador.model.criteria.Criterion
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.source.CriterionVerifier

class IndexingCriterion(
    val name: String,
    private val criteriaVerifier: CriterionVerifier,
    private val failureMessageSupplier: ((Project) -> String)? = null
) : Criterion<Project> {

    override fun getFailureMessage(input: Project): String =
        failureMessageSupplier?.invoke(input) ?: "Evaluation of ${this.name} criteria failed on project '${input.name}' (id=${input.id})"

    override fun evaluate(input: Project): Boolean = criteriaVerifier(input)

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean = if (other is IndexingCriterion) {
        this.name == other.name
    } else {
        false
    }

    override fun hashCode(): Int = name.hashCode()
}
