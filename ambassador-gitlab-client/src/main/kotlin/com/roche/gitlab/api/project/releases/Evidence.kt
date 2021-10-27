package com.roche.gitlab.api.project.releases

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.gitlab.api.utils.Dates
import java.time.LocalDateTime

data class Evidence(
    @JsonProperty("collected_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val collectedAt: LocalDateTime? = null,
    @JsonProperty("filepath")
    val filepath: String? = null,
    @JsonProperty("sha")
    val sha: String? = null
)
