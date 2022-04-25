package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.AdvisorContext
import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.Feature
import com.roche.ambassador.model.project.Project
import kotlin.reflect.KClass

object Dsl {

    fun <A : BuildableAdvice> advise(
        buildableAdvice: A,
        context: AdvisorContext,
        withBuilder: RulesBuilder.() -> Unit
    ) {
        val builder = RulesBuilder(buildableAdvice, context)
        withBuilder(builder)
        builder()
    }
}

internal fun <T> always(bool: Boolean, rulesBuilder: RulesBuilder, value: T? = null) = Has({ bool }, value, rulesBuilder)
fun <T> alwaysFalse(rulesBuilder: RulesBuilder) = always<T>(false, rulesBuilder, null)
fun <T> alwaysTrue(value: T, rulesBuilder: RulesBuilder) = always(true, rulesBuilder, value)
fun <T> not(predicate: T.() -> Boolean): T.() -> Boolean = { !predicate(this) }

interface ThatSupport<T> {
    infix fun that(predicate: T.() -> Boolean): Has<T>
    infix fun thatNot(predicate: T.() -> Boolean): Has<T> = that(not(predicate))
}

interface ThenSupport {
    infix fun then(adviceKey: String): Then
    fun thenDoNothing(): Then
}

interface HasSupport {
    infix fun has(predicate: Project.() -> Boolean): Has<Project>
    infix fun <T, F : Feature<T>> has(featureType: KClass<F>): HasFeature<T, F>
    infix fun hasNot(predicate: Project.() -> Boolean): Has<Project> = has(not(predicate))
}

interface MatchFirstSupport {
    infix fun matchFirst(matchFirst: MatchFirst.() -> Unit)
    fun <T> matchFirst(valueExtractor: Project.() -> T, matchFirst: MatchFirstValue<T>.() -> Unit)
    fun <T, F : Feature<T>> matchFirst(featureType: KClass<F>, matchFirst: MatchFirstFeature<T, F>.() -> Unit)
    fun <T, F : Feature<T>, U> matchFirst(
        featureType: KClass<F>,
        valueExtractor: T.() -> U,
        matchFirst: MatchFirstValue<U>.() -> Unit
    )
}

interface WithSupport {
    fun <T> with(valueExtractor: Project.() -> T, with: With<T>.() -> Unit)
    fun <T, F : Feature<T>> with(featureType: KClass<F>, with: WithFeature<T, F>.() -> Unit)
}
