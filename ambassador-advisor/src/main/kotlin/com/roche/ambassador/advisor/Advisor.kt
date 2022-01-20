package com.roche.ambassador.advisor

import com.roche.ambassador.advisor.messages.AdviceMessage

interface Advisor {

    suspend fun getAdvices(context: AdvisorContext): List<AdviceMessage>
    suspend fun advise(context: AdvisorContext)
}
