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
        withBuilder: RulesBuilder<A>.() -> Unit
    ) {
        val builder = RulesBuilder(buildableAdvice, context)
        withBuilder(builder)
        builder()
    }
}

internal fun <A : BuildableAdvice, T> always(bool: Boolean, rulesBuilder: RulesBuilder<A>, value: T? = null) = Has({ bool }, value, rulesBuilder)
fun <A : BuildableAdvice, T> alwaysFalse(rulesBuilder: RulesBuilder<A>) = always<A, T>(false, rulesBuilder, null)
fun <A : BuildableAdvice, T> alwaysTrue(value: T, rulesBuilder: RulesBuilder<A>) = always(true, rulesBuilder, value)
fun <T> not(predicate: T.() -> Boolean): T.() -> Boolean = { !predicate(this) }

interface ThatSupport<A : BuildableAdvice, T> {
    infix fun that(predicate: T.() -> Boolean): Has<A, T>
    infix fun thatNot(predicate: T.() -> Boolean): Has<A, T> = that(not(predicate))
}

interface ThenSupport {
    infix fun then(adviceKey: String): Then
    fun thenDoNothing(): Then
}

interface HasSupport<A : BuildableAdvice> {
    infix fun has(predicate: Project.() -> Boolean): Has<A, Project>
    infix fun <T, F : Feature<T>> has(featureType: KClass<F>): HasFeature<A, T, F>
    infix fun hasNot(predicate: Project.() -> Boolean): Has<A, Project> = has(not(predicate))
}

interface MatchFirstSupport<A : BuildableAdvice> {
    infix fun matchFirst(matchFirst: MatchFirst<A>.() -> Unit)
    fun <T> matchFirst(valueExtractor: Project.() -> T, matchFirst: MatchFirstValue<A, T>.() -> Unit)
    fun <T, F : Feature<T>> matchFirst(featureType: KClass<F>, matchFirst: MatchFirstFeature<A, T, F>.() -> Unit)
    fun <T, F : Feature<T>, U> matchFirst(
        featureType: KClass<F>,
        valueExtractor: T.() -> U,
        matchFirst: MatchFirstValue<A, U>.() -> Unit
    )
}

interface WithSupport<A : BuildableAdvice> {
    fun <T> with(valueExtractor: Project.() -> T, with: With<A, T>.() -> Unit)
    fun <T, F : Feature<T>> with(featureType: KClass<F>, with: WithFeature<A, T, F>.() -> Unit)
}
