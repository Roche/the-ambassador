package com.roche.ambassador.advisor.dsl

sealed class ConditionsWrapper : Invokable {
    protected val conditions: MutableList<Invokable> = mutableListOf()

    protected fun <T : Invokable> apply(invokable: T): T {
        conditions += invokable
        return invokable
    }

    override fun invoke(): Boolean {
        conditions.forEach { it() }
        return true
    }
}