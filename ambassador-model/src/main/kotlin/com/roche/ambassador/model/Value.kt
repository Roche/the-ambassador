package com.roche.ambassador.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonUnwrapped

class Value<T> private constructor(@JsonUnwrapped private val value: T?) {

    @JsonIgnore
    fun get(): T = value!!

    @JsonIgnore
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
