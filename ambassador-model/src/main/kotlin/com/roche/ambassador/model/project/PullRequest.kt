package com.roche.ambassador.model.project

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.roche.ambassador.model.stats.DurationPoint
import java.time.LocalDateTime

@JsonPropertyOrder("start", "end", "duration")
class PullRequest(start: LocalDateTime, end: LocalDateTime?, val state: State) : DurationPoint(start, end) {

    enum class State {
        MERGED,
        OPEN,
        CLOSED
    }
}