package com.roche.ambassador.model.project

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.ambassador.Durations
import com.roche.ambassador.extensions.toHumanReadable
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.ceil

data class PullRequests(val series: List<PullRequest>) {

    // TODO make includeWeekends configurable per-instance
    @JsonIgnore
    fun averageTimeToMerge(): Duration {
        val averageSeconds = series
            .map { Durations.between(it.start, it.end ?: LocalDateTime.now(), includeWeekends = true) }
            .map { it.seconds }
            .average()
        return Duration.ofSeconds(ceil(averageSeconds).toLong())
    }

    @JsonProperty("averageTimeToMerge")
    fun averageTimeToMergeAsString(): String = averageTimeToMerge().toHumanReadable()

    @JsonProperty("averageTimeToMergeSeconds")
    fun averageTimeToMergeAsSeconds(): Long = averageTimeToMerge().seconds

}