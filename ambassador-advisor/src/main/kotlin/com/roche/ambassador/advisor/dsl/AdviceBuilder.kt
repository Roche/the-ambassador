package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.AdvisorContext
import com.roche.ambassador.advisor.model.BuildableAdvice

class AdviceBuilder<A : BuildableAdvice> internal constructor(buildableAdvice: A, context: AdvisorContext) : ConditionsBuilder<A>(buildableAdvice, context) {

    infix fun matchFirst(matchFirst: MatchFirst<A>.() -> Unit) {
        val c = MatchFirst(buildableAdvice, context)
        matchFirst(c)
        apply(c)
    }

    override operator fun invoke(): Boolean {
        conditions.forEach { it() }
        return true
    }
}