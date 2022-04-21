package com.roche.ambassador.advisor.api

import com.roche.ambassador.advisor.messages.AdviceMessage

data class AdviceDto(
    val name: String,
    val details: String,
    val reason: String,
    val remediation: String,
    val severity: AdviceMessage.AdviceSeverity
)
