package com.roche.ambassador

import com.roche.ambassador.extensions.isWeekday
import java.time.Duration
import java.time.LocalDateTime

object Durations {

    private const val SECONDS_PER_DAY = 60 * 60 * 24

    fun between(
        start: LocalDateTime,
        end: LocalDateTime,
        includeWeekends: Boolean = true,
        workingTimeCoefficient: Double = 1.0
    ): Duration {
        if (includeWeekends) {
            return Duration.between(start, end)
        }
        val startDate = start.toLocalDate()
        val endDate = end.toLocalDate()
        if (startDate.isBefore(endDate)) {
            val nextDay = startDate.plusDays(1)
            val firstDaySeconds = SECONDS_PER_DAY - start.toLocalTime().toSecondOfDay() // remaining seconds until end of day
            val lastDaySeconds = end.toLocalTime().toSecondOfDay()
            val timeAdjustment = firstDaySeconds + lastDaySeconds
            if (nextDay.isEqual(endDate)) {
                return Duration.ofSeconds(timeAdjustment.toLong())
            }
            val days = nextDay.datesUntil(endDate)
                .filter { includeWeekends || it.isWeekday() }
                .count()
            val dayAdjustment = (SECONDS_PER_DAY * days * workingTimeCoefficient).toLong()
            return Duration.ofSeconds(dayAdjustment + timeAdjustment)
        } else {
            return Duration.between(start, end)
        }
    }
}
