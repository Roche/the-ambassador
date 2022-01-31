package com.roche.ambassador.advisor.dsl

data class AdviceKey(val key: String, val params: List<Any> = listOf())

infix fun String.with(params: List<Any>): AdviceKey = AdviceKey(this, params)

infix fun String.with(param: Any): AdviceKey = AdviceKey(this, listOf(param))
