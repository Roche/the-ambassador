package com.roche.ambassador.advisor.messages

data class AdviceMessage(
    var name: String,
    var details: String,
    var reason: String,
    var remediation: String,
    var priority: AdvicePriority,
) {

    enum class AdvicePriority {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW
    }

}