package com.roche.ambassador.advisor.dsl

open class MatchFirst internal constructor(parent: RulesBuilder) : RulesBuilder(parent) {

    fun or(withBuilder: RulesBuilder.() -> Unit) {
        val builder = RulesBuilder(this)
        withBuilder(builder)
        apply(builder)
    }

    open fun orMatchingFirst(withBuilder: MatchFirst.() -> Unit) {
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
