package com.roche.gitlab.api.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Statistics(
    @JsonProperty("counts")
    val counts: Counts
)
