package com.roche.ambassador.advisor

interface Advisor {

    suspend fun advise(context: AdvisorContext)

}