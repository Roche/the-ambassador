package com.roche.ambassador.events

import java.util.*

data class AmbassadorEvent<T>(
    val id: UUID,
    val timestamp: Long = System.currentTimeMillis(),
    val payload: T
)
