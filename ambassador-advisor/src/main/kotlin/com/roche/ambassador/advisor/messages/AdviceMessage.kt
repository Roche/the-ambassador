package com.roche.ambassador.advisor.messages

import com.roche.ambassador.advisor.common.Color

data class AdviceMessage(
    var name: String,
    var details: String,
    var reason: String,
    var remediation: String,
    var severity: AdviceSeverity,
    var severityBadge: String
) {

    enum class AdviceSeverity(val level: Int, val color: Color) {
        CRITICAL(0, Color.RED),
        HIGH(1, Color.ORANGE),
        MEDIUM(2, Color.YELLOW),
        LOW(3, Color.BLUE)
    }

}