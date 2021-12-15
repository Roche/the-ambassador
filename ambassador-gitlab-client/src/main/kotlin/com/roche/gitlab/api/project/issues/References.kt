package com.roche.gitlab.api.project.issues

import com.fasterxml.jackson.annotation.JsonProperty

data class References(
    @JsonProperty("full")
    val full: String? = null,
    @JsonProperty("relative")
    val relative: String? = null,
    @JsonProperty("short")
    val short: String? = null
)