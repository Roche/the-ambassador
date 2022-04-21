package com.roche.ambassador.model.dataproviders

import com.roche.ambassador.ClockHolder
import java.time.*

object DateAccessor {
    private val clockInstant = Instant.parse("2022-04-10T16:00:00.00Z")
    val clock: Clock = Clock.fixed(clockInstant, ZoneId.systemDefault())
    init {
        ClockHolder.clock = clock
    }

    fun nowDate(): LocalDate = LocalDate.now(clock)

    fun nowDateTime(): LocalDateTime = LocalDateTime.now(clock)
}

fun nowDate() = DateAccessor.nowDate()
fun nowDateTime() = DateAccessor.nowDateTime()
