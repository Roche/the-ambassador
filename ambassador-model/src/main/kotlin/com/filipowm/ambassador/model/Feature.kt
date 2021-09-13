package com.filipowm.ambassador.model

import com.filipowm.ambassador.extensions.toCamelCase

interface Feature<T> : Specification, Explainable, Weighted, Indexable {

    fun importance(): Importance
    fun value(): T?
    override fun makeIndexable(): Pair<String, Any>? {
        val value = value() ?: return null
        return Pair(name().toCamelCase(), value as Any)
    }

    fun exists(): Boolean = value() != null

}