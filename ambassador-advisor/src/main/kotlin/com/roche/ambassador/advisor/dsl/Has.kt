package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.AdvisorContext
import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.project.Project

class Has<A : BuildableAdvice, T> internal constructor(
    private val predicate: T.() -> Boolean,
    private val testValue: T?,
    private val adviceBuilder: ConditionsBuilder<A>
) : Invokable {

    private var action: (AdvisorContext) -> Unit = {}

    infix fun and(andPredicate: T.() -> Boolean): Has<A, T> = Has({ predicate(this) && andPredicate(this) }, testValue, adviceBuilder)

    infix fun or(orPredicate: T.() -> Boolean): Has<A, T> = Has({ predicate(this) || orPredicate(this) }, testValue, adviceBuilder)

    infix fun then(adviceKey: String) {
        thenProvided {
            AdviceKey(adviceKey)
        }
    }

    infix fun thenProvided(keyProvider: Project.() -> AdviceKey) {
        then {
            val key = keyProvider(it.project)
            val config = it.getAdviceConfig(key)
            adviceBuilder.buildableAdvice.apply(config)
        }
    }

    infix fun then(action: (AdvisorContext) -> Unit) {
        this.action = action
    }

    override operator fun invoke(): Boolean {
        if (testValue != null && predicate(testValue)) {
            action(adviceBuilder.context)
            return true
        }
        return false
    }
}