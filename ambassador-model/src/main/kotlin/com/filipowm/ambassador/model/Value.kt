package com.filipowm.ambassador.model

class Value<T> private constructor(private val value: T?) {

    fun get(): T = value!!
    fun exists(): Boolean = value != null

    companion object {
        fun <T> of(value: T?): Value<T> {
            return Value(value)
        }
    }

}