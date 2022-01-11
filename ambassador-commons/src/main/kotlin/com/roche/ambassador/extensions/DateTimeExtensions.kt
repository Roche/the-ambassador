package com.roche.ambassador.extensions

import com.roche.ambassador.Durations
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import java.util.*

fun Date.toZonedDateTime(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime = this.toInstant().atZone(zone)
fun Date.toLocalDate(zone: ZoneId = ZoneId.systemDefault()): LocalDate = toZonedDateTime(zone).toLocalDate()
fun Date.toLocalDateTime(zone: ZoneId = ZoneId.systemDefault()): LocalDateTime = toZonedDateTime(zone).toLocalDateTime()

fun LocalDate.toDate(zone: ZoneId = ZoneId.systemDefault()): Date = Date.from(this.atStartOfDay(zone).toInstant())
fun LocalDateTime.toDate(zone: ZoneId = ZoneId.systemDefault()): Date = Date.from(this.atZone(zone).toInstant())
fun ZonedDateTime.toDate(): Date = Date.from(this.toInstant())

fun Temporal.daysUntilNow(): Long = unitsUntilNow(ChronoUnit.DAYS)
fun Temporal.monthsUntilNow(): Long = unitsUntilNow(ChronoUnit.MONTHS)
fun Temporal.unitsUntilNow(chronoUnit: ChronoUnit): Long = chronoUnit.between(this, LocalDate.now())

fun LocalDate.isWeekend(): Boolean = dayOfWeek in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

fun LocalDate.isWeekday(): Boolean = !isWeekend()

fun LocalDateTime.between(other: LocalDateTime, includeWeekends: Boolean = true, workingTimeCoefficient: Double = 1.0): Duration {
    return if (isAfter(other)) {
        Durations.between(other, this, includeWeekends = includeWeekends, workingTimeCoefficient = workingTimeCoefficient)
    } else {
        Durations.between(this, other, includeWeekends = includeWeekends, workingTimeCoefficient = workingTimeCoefficient)
    }
}