package com.roche.ambassador.model

interface Feature<T> : Specification, Explainable, Weighted {

    fun importance(): Importance
    fun value(): Value<T>

    fun exists(): Boolean = value().exists()

    fun isIndexable(): Boolean = true

    fun ifExists(handler: (Feature<T>) -> Unit) {
        if (exists()) {
            handler(this)
        }
    }

    fun withValue(handler: (T) -> Unit) {
        if (exists()) {
            handler(this.value().get())
        }
    }
}
