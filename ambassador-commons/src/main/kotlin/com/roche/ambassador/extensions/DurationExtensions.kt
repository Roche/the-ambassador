package com.roche.ambassador.extensions

import java.time.Duration
import java.util.stream.Collectors
import java.util.stream.Stream

fun Duration.toHumanReadable(withMillis: Boolean = false, delimeter: String = " "): String {
    val days = toDays()
    val hours = toHours() - days * 24
    val minutes = toMinutes() - toHours() * 60
    val seconds = toSeconds() - toMinutes() * 60
    val millis = if (withMillis) {
        toMillis() - toSeconds() * 1000
    } else {
        0
    }
    return DurationStringBuilder()
        .delimeter(delimeter)
        .days(days)
        .hours(hours)
        .minutes(minutes)
        .seconds(seconds)
        .millis(millis)
        .build()
}

class DurationStringBuilder {
    private var days: String? = null
    private var hours: String? = null
    private var minutes: String? = null
    private var seconds: String? = null
    private var millis: String? = null
    private var delimeter: String = " "

    fun delimeter(delimeter: String): DurationStringBuilder {
        this.delimeter = delimeter
        return this
    }

    fun days(days: Long): DurationStringBuilder {
        this.days = applyTime(days, "d")
        return this
    }

    fun hours(hours: Long): DurationStringBuilder {
        this.hours = applyTime(hours, "h")
        return this
    }

    fun minutes(minutes: Long): DurationStringBuilder {
        this.minutes = applyTime(minutes, "m")
        return this
    }

    fun seconds(seconds: Long): DurationStringBuilder {
        this.seconds = applyTime(seconds, "s")
        return this
    }

    fun millis(seconds: Long): DurationStringBuilder {
        this.millis = applyTime(seconds, "ms")
        return this
    }

    fun build(): String {
        val result = Stream.of(days, hours, minutes, seconds, millis)
            .filter { !it.isNullOrBlank() }
            .collect(Collectors.joining(delimeter))
        return if (result.isBlank()) {
            "0s"
        } else {
            result
        }
    }

    private fun applyTime(time: Long, literal: String): String? {
        return if (time > 0) {
            "${time}${literal}"
        } else {
            null
        }
    }
}
