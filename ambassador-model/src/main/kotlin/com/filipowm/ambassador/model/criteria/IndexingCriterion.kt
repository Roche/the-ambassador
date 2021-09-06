package com.filipowm.ambassador.model.criteria

interface IndexingCriterion<T>: Criterion<T> {

    fun onCriterionFailure(input: T) {
        // do nothing
    }

    override fun getFailureMessage(input: T): String = "Evaluation of ${this.getName()} criteria failed"

    fun getName(): String = this.javaClass.simpleName

    companion object {
        fun <T> asIndexingCriterion(criterion: Criterion<T>, onCriterionFailure: (T) -> Unit = {}): IndexingCriterion<T> = DelegatedIndexingCriterion(criterion, onCriterionFailure)
    }

    private class DelegatedIndexingCriterion<T>(private val criterion: Criterion<T>, private val onCriterionFailure: (T) -> Unit = {}): IndexingCriterion<T>, Criterion<T> by criterion {
        override fun getFailureMessage(input: T): String = criterion.getFailureMessage(input)

        override fun onCriterionFailure(input: T) {
            this.onCriterionFailure.invoke(input)
        }
    }
}