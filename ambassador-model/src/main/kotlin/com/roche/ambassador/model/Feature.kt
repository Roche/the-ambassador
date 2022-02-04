package com.roche.ambassador.model

interface Feature<T> : Specification, Weighted {

    fun importance(): Importance
    fun value(): Value<T>

    fun exists(): Boolean = value().exists()

    fun isIndexable(): Boolean = true

    fun withValue(handler: (T) -> Unit) {
        if (exists()) {
            handler(this.value().get())
        }
    }
}
