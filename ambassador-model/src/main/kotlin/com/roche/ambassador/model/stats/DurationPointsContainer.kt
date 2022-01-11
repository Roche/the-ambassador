package com.roche.ambassador.model.stats

import com.fasterxml.jackson.annotation.JsonIgnore
import com.roche.ambassador.Durations
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.ceil

open class DurationPointsContainer<T : DurationPoint>(val data: List<T>) {

    @JsonIgnore
    protected val averageDuration: Duration

    init {
        val averageSeconds = data
            .map { Durations.between(it.start, it.end ?: LocalDateTime.now(), includeWeekends = true) }
            .map { it.seconds }
            .average()
        this.averageDuration = Duration.ofSeconds(ceil(averageSeconds).toLong())
    }
}