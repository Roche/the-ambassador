package com.roche.ambassador.model.project

import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.ambassador.extensions.toHumanReadable
import com.roche.ambassador.model.stats.DurationPointsContainer

class PullRequests(data: List<PullRequest>) : DurationPointsContainer<PullRequest>(data) {

    @JsonProperty("averageTimeToMerge")
    fun averageTimeToMergeAsString(): String = averageDuration.toHumanReadable()

    @JsonProperty("averageTimeToMergeSeconds")
    fun averageTimeToMergeAsSeconds(): Long = averageDuration.seconds
}
