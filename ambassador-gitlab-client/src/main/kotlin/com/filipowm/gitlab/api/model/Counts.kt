package com.filipowm.gitlab.api.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Counts(
    @JsonProperty("closed")
    val closed: Int = 0,
    @JsonProperty("opened")
    val opened: Int = 0,
    @JsonProperty("all")
    val all: Int = opened + closed
)
