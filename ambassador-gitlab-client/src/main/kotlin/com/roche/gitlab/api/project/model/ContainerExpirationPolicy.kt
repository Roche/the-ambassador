package com.roche.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.gitlab.api.utils.Dates.ISO_DATE_TIME_FORMAT
import java.time.LocalDateTime

data class ContainerExpirationPolicy(
    @JsonProperty("cadence")
    var cadence: String?,
    @JsonProperty("enabled")
    var enabled: Boolean?,
    @JsonProperty("keep_n")
    var keepN: Int?,
    @JsonProperty("name_regex")
    var nameRegex: String?,
    @JsonProperty("name_regex_delete")
    var nameRegexDelete: String?,
    @JsonProperty("name_regex_keep")
    var nameRegexKeep: String?,
    @JsonProperty("next_run_at")
    @JsonFormat(pattern = ISO_DATE_TIME_FORMAT)
    var nextRunAt: LocalDateTime?,
    @JsonProperty("older_than")
    var olderThan: String?
)
