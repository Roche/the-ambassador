package com.roche.ambassador.model.project.ci

import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.ambassador.extensions.toHumanReadable
import com.roche.ambassador.model.stats.DurationPointsContainer

class CiExecutions(
    data: List<CiExecution>,
    val stability: CiStability?
) : DurationPointsContainer<CiExecution>(data) {

    @JsonProperty("averageDuration")
    fun averageDurationAsString(): String = averageDuration.toHumanReadable()

    @JsonProperty("averageDurationSeconds")
    fun averageDurationAsSeconds(): Long = averageDuration.seconds

}