package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.project.Project

class With<T>(
    private val valueExtractor: Project.() -> T?,
    private val rulesBuilder: RulesBuilder
) : ConditionsWrapper(), ThatSupport<T> {

    override infix fun that(predicate: T.() -> Boolean): Has<T> {
        val value = valueExtractor(rulesBuilder.context.project)
        val has: Has<T> = if (value == null) {
            rulesBuilder.alwaysFalse()
        } else {
            Has(predicate, value, rulesBuilder)
        }
        apply(has)
        return has
    }
}
