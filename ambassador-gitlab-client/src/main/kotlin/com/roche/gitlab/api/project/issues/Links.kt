package com.roche.gitlab.api.project.issues

import com.fasterxml.jackson.annotation.JsonProperty

data class Links(
    @JsonProperty("award_emoji")
    val awardEmoji: String? = null,
    @JsonProperty("notes")
    val notes: String? = null,
    @JsonProperty("project")
    val project: String? = null,
    @JsonProperty("self")
    val self: String? = null
)