package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.model.BuildableAdvice

class Has<A : BuildableAdvice, T> internal constructor(
    private val predicate: T.() -> Boolean,
    private val testValue: T?,
    private val rulesBuilder: RulesBuilder<A>
) : Invokable, ThenSupport<A> {

    private var action: Invokable? = null

    infix fun and(andPredicate: T.() -> Boolean): Has<A, T> = Has({ predicate(this) && andPredicate(this) }, testValue, rulesBuilder)

    infix fun or(orPredicate: T.() -> Boolean): Has<A, T> = Has({ predicate(this) || orPredicate(this) }, testValue, rulesBuilder)

    override infix fun then(adviceKey: String): Then<A> {
        val then = Then(adviceKey, rulesBuilder)
        this.action = then
        return then
    }

    override operator fun invoke(): Boolean {
        if (testValue != null && action != null && predicate(testValue)) {
            action!!()
            return true
        }
        return false
    }
}