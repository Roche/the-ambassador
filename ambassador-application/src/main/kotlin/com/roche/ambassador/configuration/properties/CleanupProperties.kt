package com.roche.ambassador.configuration.properties

import java.time.Period

data class CleanupProperties(
    val enabled: Boolean = true,
    val cleanupOlderThan: Period = Period.ofMonths(6),
    val cron: String = "0 0 1 ? * WED,SAT"
)
