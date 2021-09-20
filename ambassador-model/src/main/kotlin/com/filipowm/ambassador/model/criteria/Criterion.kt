package com.filipowm.ambassador.model.criteria

import java.util.function.Predicate

interface Criterion<T> : Predicate<T> {

    fun getFailureMessage(input: T): String = "Evaluation of ${this.javaClass.simpleName} criterion failed"
    fun evaluate(input: T): Boolean

    override fun test(p0: T): Boolean = evaluate(p0)

    fun and(other: Criterion<T>): Criterion<T> = AndCriterion(this, other)
    fun or(other: Criterion<T>): Criterion<T> = OrCriterion(this, other)
    fun not(): Criterion<T> = NotCriterion(this)

    companion object {
        fun <T> not(criterion: Criterion<T>): Criterion<T> = criterion.not()
    }
}