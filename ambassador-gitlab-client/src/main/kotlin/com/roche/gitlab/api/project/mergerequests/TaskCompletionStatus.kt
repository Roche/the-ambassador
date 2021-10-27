package com.roche.gitlab.api.project.mergerequests


import com.fasterxml.jackson.annotation.JsonProperty

data class TaskCompletionStatus(
    @JsonProperty("completed_count")
    val completedCount: Int? = null,
    @JsonProperty("count")
    val count: Int? = null
)