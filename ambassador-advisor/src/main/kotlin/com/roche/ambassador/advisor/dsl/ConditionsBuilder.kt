package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.AdvisorContext
import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.Feature
import com.roche.ambassador.model.project.Project
import kotlin.reflect.KClass

sealed class ConditionsBuilder<A : BuildableAdvice> constructor(
    val buildableAdvice: A,
    val context: AdvisorContext
) : Invokable {

    protected val conditions: MutableList<Invokable> = mutableListOf()

    protected fun apply(invokable: Invokable) {
        conditions += invokable
    }

    fun <T> alwaysFalse() = alwaysFalse<A, T>(this)

    fun <T> alwaysTrue() = alwaysFalse<A, T>(this)

    infix fun has(predicate: Project.() -> Boolean): Has<A, Project> {
        val has = Has(predicate, context.project, this)
        apply(has)
        return has
    }

    infix fun <T, F : Feature<T>> has(featureType: KClass<F>): HasFeature<A, T, F> {
        val has = HasFeature(featureType, this)
        apply(has)
        return has
    }

    infix fun hasNot(predicate: Project.() -> Boolean): Has<A, Project> = has(not(predicate))

}