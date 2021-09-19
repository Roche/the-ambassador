package com.filipowm.ambassador.model

data class IndexEntry private constructor (val key: String, val value: Any?) {

    fun isValid() = value != null

    fun with(handler: (String, Any) -> Unit) {
        if (isValid()) {
            handler.invoke(key, value!!)
        }
    }

    companion object {
        fun no() = IndexEntry("__noindex__", null)
        fun of(key: String, value: Any) = IndexEntry(key, value)
    }

}