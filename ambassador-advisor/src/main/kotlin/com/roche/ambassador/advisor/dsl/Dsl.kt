package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.AdvisorContext
import com.roche.ambassador.advisor.model.BuildableAdvice

object Dsl {

    fun <A : BuildableAdvice> advise(buildableAdvice: A, context: AdvisorContext, withBuilder: AdviceBuilder<A>.() -> Unit) {
        val builder = AdviceBuilder(buildableAdvice, context)
        withBuilder(builder)
        builder()
    }
}
internal fun <A : BuildableAdvice, T> always(bool: Boolean, conditionsBuilder: ConditionsBuilder<A>) = Has({ bool }, null as T, conditionsBuilder)
fun <A : BuildableAdvice, T> alwaysFalse(conditionsBuilder: ConditionsBuilder<A>) = always<A, T>(false, conditionsBuilder)
fun <A : BuildableAdvice, T> alwaysTrue(conditionsBuilder: ConditionsBuilder<A>) = always<A, T>(true, conditionsBuilder)
fun <T> not(predicate: T.() -> Boolean): T.() -> Boolean  = { !predicate(this) }
