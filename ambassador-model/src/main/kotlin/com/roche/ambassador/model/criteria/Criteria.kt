package com.roche.ambassador.model.criteria

interface Criteria<T> {

    fun evaluate(input: T): Boolean
}
