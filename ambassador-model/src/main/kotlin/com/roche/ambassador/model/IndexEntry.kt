package com.roche.ambassador.model

import com.roche.ambassador.extensions.toCamelCase

data class IndexEntry internal constructor(val key: String, val value: Any?) {

    fun isValid(): Boolean = value != null

    fun with(handler: (String, Any) -> Unit) {
        if (isValid()) {
            handler.invoke(key, value ?: return)
        }
    }

    companion object {
        fun no(): IndexEntry = IndexEntry("__noindex__", null)
        fun of(key: String, value: Any): IndexEntry = IndexEntry(key.toCamelCase(), value)
    }

}