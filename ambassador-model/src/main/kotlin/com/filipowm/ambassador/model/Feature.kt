package com.filipowm.ambassador.model

import com.filipowm.ambassador.extensions.toCamelCase

interface Feature<T> : Specification, Explainable, Weighted, Indexable {

    fun importance(): Importance
    fun value(): T?
    override fun asIndexEntry(): IndexEntry {
        val value = value() ?: return IndexEntry.no()
        return IndexEntry.of(name().toCamelCase(), value)
    }

    fun exists(): Boolean = value() != null

}