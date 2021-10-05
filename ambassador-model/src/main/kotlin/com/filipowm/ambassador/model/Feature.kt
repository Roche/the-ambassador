package com.filipowm.ambassador.model

import com.filipowm.ambassador.extensions.toCamelCase

interface Feature<T> : Specification, Explainable, Weighted, Indexable {

    fun importance(): Importance
    fun value(): Value<T>
    override fun asIndexEntry(): IndexEntry {
        val value = value()
        if (value.exists()) {
            return IndexEntry.of(name().toCamelCase(), value)
        }
        return IndexEntry.no()
    }

    fun exists(): Boolean = value().exists()

    fun ifExists(handler: (Feature<T>) -> Unit) {
        if (exists()) {
            handler(this)
        }
    }

}