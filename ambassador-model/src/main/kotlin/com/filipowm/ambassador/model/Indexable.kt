package com.filipowm.ambassador.model

interface Indexable {

    fun makeIndexable(): Pair<String, Any>?

}