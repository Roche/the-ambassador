package com.roche.ambassador.advisor.model

import com.roche.ambassador.advisor.messages.AdviceMessage

interface BuildableAdvice {

    fun apply(message: AdviceMessage)

}