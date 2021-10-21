package com.filipowm.gitlab.api.project.mergerequests


import com.fasterxml.jackson.annotation.JsonProperty

data class TimeStats(
    @JsonProperty("human_time_estimate")
    val humanTimeEstimate: String? = null,
    @JsonProperty("human_total_time_spent")
    val humanTotalTimeSpent: String? = null,
    @JsonProperty("time_estimate")
    val timeEstimate: Int? = null,
    @JsonProperty("total_time_spent")
    val totalTimeSpent: Int? = null
)