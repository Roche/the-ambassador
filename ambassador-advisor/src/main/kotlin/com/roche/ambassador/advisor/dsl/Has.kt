package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.model.BuildableAdvice

typealias Predicate<T> = T.() -> Boolean

class Has<A : BuildableAdvice, T> internal constructor(
    predicate: Predicate<T>,
    private val testValue: T?,
    private val rulesBuilder: RulesBuilder<A>
) : Invokable, ThenSupport {

    private val chainedPredicates: PredicateChain<T> = PredicateChain(predicate, testValue)
    private var action: Invokable? = null

    infix fun and(andPredicate: Predicate<T>): Has<A, T> {
        chainedPredicates.and(andPredicate)
        return this
    }

    override fun thenDoNothing(): Then {
        val then = Then.nothing()
        this.action = then
        return then
    }

    override infix fun then(adviceKey: String): Then {
        val then = Then.adviceMessage(adviceKey, rulesBuilder)
        this.action = then
        return then
    }

    override operator fun invoke(): Boolean {
        if (testValue != null && action != null && chainedPredicates.invoke()) {
            action!!()
            return true
        }
        return false
    }

    private class PredicateChain<T>(initial: Predicate<T>, val target: T?) : Invokable {

        private val and: MutableList<Predicate<T>> = mutableListOf(initial)

        fun and(predicate: Predicate<T>) {
            and.add(predicate)
        }

        override fun invoke(): Boolean {
            return target != null && and.all { it(target) }
        }
    }
}
