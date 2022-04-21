package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.project.Project

class MatchFirstValue<A : BuildableAdvice, T>(
    private val valueExtractor: Project.() -> T?,
    private val rulesBuilder: RulesBuilder<A>
) : MatchFirst<A>(rulesBuilder), ThatSupport<A, T> {

    override infix fun that(predicate: T.() -> Boolean): Has<A, T> {
        val value = valueExtractor(context.project)
        val has: Has<A, T> = if (value == null) {
            rulesBuilder.alwaysFalse()
        } else {
            Has(predicate, value, rulesBuilder)
        }
        apply(has)
        return has
    }
}
