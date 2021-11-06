package com.roche.ambassador.model.criteria

class NotCriterion<T>(private val delegate: Criterion<T>) : Criterion<T> {

    override fun getFailureMessage(input: T): String = "Negated criteria failed: ${delegate.getFailureMessage(input)}"

    override fun evaluate(input: T): Boolean = !delegate.evaluate(input)
}
