package com.filipowm.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Source(
    @JsonProperty("format")
    val format: String? = null,
    @JsonProperty("url")
    val url: String? = null
)
