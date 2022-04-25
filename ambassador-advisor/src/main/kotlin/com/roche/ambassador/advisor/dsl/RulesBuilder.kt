package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.AdvisorContext
import com.roche.ambassador.advisor.configuration.RulesProperties
import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.Feature
import com.roche.ambassador.model.project.Project
import java.util.*
import kotlin.reflect.KClass

open class RulesBuilder constructor(
    val buildableAdvice: BuildableAdvice,
    val context: AdvisorContext
) : ConditionsWrapper(), HasSupport, MatchFirstSupport, WithSupport {

    constructor(parent: RulesBuilder) : this(parent.buildableAdvice, parent.context)

    fun <T, F : Feature<T>> readFeature(featureType: KClass<F>): Optional<T> = context.project.features.findValue(featureType)

    val config = context.rulesConfiguration

    fun <T> alwaysFalse() = apply(alwaysFalse<T>(this))

    fun anyAlwaysFalse() = this.alwaysFalse<Any>()

    fun anyAlwaysTrue() = apply(alwaysTrue(Any(), this))

    fun whenEnabled(rule: RulesProperties.Rule, handler: RulesBuilder.() -> Unit) {
        if (rule.enabled) {
            handler(this)
        }
    }

    override fun <T> with(valueExtractor: Project.() -> T, with: With<T>.() -> Unit) {
        val handler = With(valueExtractor, this)
        with(handler)
        apply(handler)
    }

    override fun <T, F : Feature<T>> with(featureType: KClass<F>, with: WithFeature<T, F>.() -> Unit) {
        val handler = WithFeature(featureType, this)
        with(handler)
        apply(handler)
    }

    override infix fun has(predicate: Project.() -> Boolean): Has<Project> {
        val has = Has(predicate, context.project, this)
        return apply(has)
    }

    override infix fun <T, F : Feature<T>> has(featureType: KClass<F>): HasFeature<T, F> {
        val has = HasFeature(featureType, this)
        return apply(has)
    }

    override infix fun matchFirst(matchFirst: MatchFirst.() -> Unit) {
        val handler = MatchFirst(this)
        matchFirst(handler)
        apply(handler)
    }

    override fun <T> matchFirst(valueExtractor: Project.() -> T, matchFirst: MatchFirstValue<T>.() -> Unit) {
        val handler = MatchFirstValue(valueExtractor, this)
        matchFirst(handler)
        apply(handler)
    }

    override fun <T, F : Feature<T>> matchFirst(
        featureType: KClass<F>,
        matchFirst: MatchFirstFeature<T, F>.() -> Unit
    ) {
        val handler = MatchFirstFeature(featureType, this)
        matchFirst(handler)
        apply(handler)
    }

    override fun <T, F : Feature<T>, U> matchFirst(
        featureType: KClass<F>,
        valueExtractor: T.() -> U,
        matchFirst: MatchFirstValue<U>.() -> Unit
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
