package com.roche.ambassador.events

import java.util.*

abstract class Event<T>(val id: UUID = UUID.randomUUID(),
                        val timestamp: Long = System.currentTimeMillis(),
                        val data: T)