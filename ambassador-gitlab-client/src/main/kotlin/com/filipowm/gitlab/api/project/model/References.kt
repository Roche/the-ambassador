package com.filipowm.gitlab.api.project.model


import com.fasterxml.jackson.annotation.JsonProperty

data class References(
    @JsonProperty("full")
    val full: String? = null,
    @JsonProperty("relative")
    val relative: String? = null,
    @JsonProperty("short")
    val short: String? = null
)