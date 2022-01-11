package com.roche.ambassador.model.project.ci

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.roche.ambassador.model.stats.DurationPoint
import java.time.LocalDateTime

@JsonPropertyOrder("start", "end", "duration")
class CiExecution(start: LocalDateTime, end: LocalDateTime?, val state: State) : DurationPoint(start, end) {

    enum class State {
        SUCCESS,
        FAILURE,
        CANCELED,
        IN_PROGRESS,
        UNKNOWN
    }
}
