package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.AdvisorContext
import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.Feature
import com.roche.ambassador.model.project.Project
import java.util.*
import kotlin.reflect.KClass

open class RulesBuilder<A : BuildableAdvice> constructor(
    val buildableAdvice: A, val context: AdvisorContext
) : ConditionsWrapper(), HasSupport<A>, MatchFirstSupport<A>, WithSupport<A> {

    constructor(parent: RulesBuilder<A>) : this(parent.buildableAdvice, parent.context)

    fun <T, F : Feature<T>> readFeature(featureType: KClass<F>): Optional<T> = context.project.features.findValue(featureType)

    fun <T> alwaysFalse() = apply(alwaysFalse<A, T>(this))

    fun anyAlwaysFalse() = this.alwaysFalse<Any>()

    fun anyAlwaysTrue() = apply(alwaysTrue(Any(), this))

    override fun <T> with(valueExtractor: Project.() -> T, with: With<A, T>.() -> Unit) {
        val handler = With(valueExtractor, this)
        with(handler)
        apply(handler)
    }

    override fun <T, F : Feature<T>> with(featureType: KClass<F>, with: WithFeature<A, T, F>.() -> Unit) {
        val handler = WithFeature(featureType, this)
        with(handler)
        apply(handler)
    }

    override infix fun has(predicate: Project.() -> Boolean): Has<A, Project> {
        val has = Has(predicate, context.project, this)
        return apply(has)
    }

    override infix fun <T, F : Feature<T>> has(featureType: KClass<F>): HasFeature<A, T, F> {
        val has = HasFeature(featureType, this)
        return apply(has)
    }

    override infix fun matchFirst(matchFirst: MatchFirst<A>.() -> Unit) {
        val handler = MatchFirst(this)
        matchFirst(handler)
        apply(handler)
    }

    override fun <T> matchFirst(valueExtractor: Project.() -> T, matchFirst: MatchFirstValue<A, T>.() -> Unit) {
        val handler = MatchFirstValue(valueExtractor, this)
        matchFirst(handler)
        apply(handler)
    }

    override fun <T, F : Feature<T>> matchFirst(featureType: KClass<F>, matchFirst: MatchFirstFeature<A, T, F>.() -> Unit) {
        val handler = MatchFirstFeature(featureType, this)
        matchFirst(handler)
        apply(handler)
    }

    override fun <T, F : Feature<T>, U> matchFirst(
        featureType: KClass<F>,
        valueExtractor: T.() -> U,
        matchFirst: MatchFirstValue<A, U>.() -> Unit
    ) {
        val wrappedFeatureExtractor: Project.() -> U = {
            val featureValue = readFeature(featureType)
            featureValue
                .map { valueExtractor(it) }
                .orElse(null)
        }
        val handler = MatchFirstValue(wrappedFeatureExtractor, this)
        matchFirst(handler)
        apply(handler)
    }

    override operator fun invoke(): Boolean {
        conditions.forEach { it() }
        return true
    }
}
