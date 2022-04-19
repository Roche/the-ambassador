package com.roche.ambassador.configuration.properties

import java.time.Duration

data class SchedulerProperties(
    val enabled: Boolean = false,
    val cron: String = "0 0 14 ? * SUN",
    val lockFor: Duration = Duration.ofMinutes(30)
)