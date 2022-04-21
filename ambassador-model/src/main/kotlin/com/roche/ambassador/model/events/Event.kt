package com.roche.ambassador.model.events

import java.util.*

abstract class Event<T>(
    val id: UUID = UUID.randomUUID(),
    val timestamp: Long = System.currentTimeMillis(),
    val data: T
)

abstract class EmptyEvent : Event<Unit>(data = Unit)
