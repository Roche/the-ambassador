package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.model.BuildableAdvice

open class MatchFirst<A : BuildableAdvice> internal constructor(parent: RulesBuilder<A>) : RulesBuilder<A>(parent) {

    fun or(withBuilder: RulesBuilder<A>.() -> Unit) {
        val builder = RulesBuilder(this)
        withBuilder(builder)
        apply(builder)
    }

    open fun orMatchingFirst(withBuilder: MatchFirst<A>.() -> Unit) {
        val builder = MatchFirst(this)
        withBuilder(builder)
        apply(builder)
    }

    override operator fun invoke(): Boolean {
        for (condition in conditions) {
            if (condition()) {
                return true
            }
        }
        return false
    }
}
