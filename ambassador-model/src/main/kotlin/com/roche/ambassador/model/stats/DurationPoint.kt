package com.roche.ambassador.model.stats

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.ambassador.extensions.toHumanReadable
import java.time.Duration
import java.time.LocalDateTime

open class DurationPoint(val start: LocalDateTime, val end: LocalDateTime?) {

    @JsonIgnore
    fun duration(): Duration = Duration.between(start, end ?: LocalDateTime.now())

    @JsonProperty("duration")
    fun durationAsString(): String = duration().toHumanReadable()

}