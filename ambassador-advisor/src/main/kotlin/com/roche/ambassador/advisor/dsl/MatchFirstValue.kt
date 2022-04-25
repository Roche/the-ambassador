package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.project.Project

class MatchFirstValue<T>(
    private val valueExtractor: Project.() -> T?,
    private val rulesBuilder: RulesBuilder
) : MatchFirst(rulesBuilder), ThatSupport<T> {

    override infix fun that(predicate: T.() -> Boolean): Has<T> {
        val value = valueExtractor(context.project)
        val has: Has<T> = if (value == null) {
            rulesBuilder.alwaysFalse()
        } else {
            Has(predicate, value, rulesBuilder)
        }
        apply(has)
        return has
    }
}
