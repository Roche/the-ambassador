package com.roche.ambassador.model

class Value<T> private constructor(private val value: T?) {

    fun get(): T = value!!
    fun exists(): Boolean = value != null

    fun orElse(elseHandler: () -> T): T {
        return if (exists()) {
            get()
        } else {
            elseHandler()
        }
    }

    companion object {
        fun <T> of(value: T?): Value<T> {
            return Value(value)
        }
    }

}