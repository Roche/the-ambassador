package com.roche.ambassador.health

import kotlin.jvm.Throws

interface Pingable {

    @Throws(UnhealthyComponentException::class)
    suspend fun ping()

}