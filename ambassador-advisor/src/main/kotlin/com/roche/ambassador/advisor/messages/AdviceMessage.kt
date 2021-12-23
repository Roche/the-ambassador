package com.roche.ambassador.advisor.messages

data class AdviceMessage(
    var name: String,
    var details: String,
    var reason: String,
    var remediation: String,
    var priority: AdvicePriority,
) {

    enum class AdvicePriority(val level: Int) {
        CRITICAL(0),
        HIGH(1),
        MEDIUM(2),
        LOW(3)
    }

}