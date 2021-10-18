package com.filipowm.gitlab.api.project.model


import com.fasterxml.jackson.annotation.JsonProperty

data class TaskCompletionStatus(
    @JsonProperty("completed_count")
    val completedCount: Int? = null,
    @JsonProperty("count")
    val count: Int? = null
)