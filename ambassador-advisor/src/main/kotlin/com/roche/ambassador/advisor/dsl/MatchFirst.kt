package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.AdvisorContext
import com.roche.ambassador.advisor.model.BuildableAdvice

class MatchFirst<A : BuildableAdvice> internal constructor(
    buildableAdvice: A,
    context: AdvisorContext,
) : ConditionsBuilder<A>(buildableAdvice, context) {

    override operator fun invoke(): Boolean {
        for (condition in conditions) {
            if (condition()) {
                return true
            }
        }
        return false
    }
}
