package com.roche.ambassador.health

import kotlin.jvm.Throws

interface Pingable {

    fun name(): String

    @Throws(UnhealthyComponentException::class)
    suspend fun ping()
}
