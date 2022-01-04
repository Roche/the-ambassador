package com.roche.gitlab.api.project.events

import com.fasterxml.jackson.annotation.JsonProperty

data class PushData(
    @JsonProperty("action")
    val action: String? = null,
    @JsonProperty("commit_count")
    val commitCount: Int? = null,
    @JsonProperty("commit_from")
    val commitFrom: String? = null,
    @JsonProperty("commit_title")
    val commitTitle: String? = null,
    @JsonProperty("commit_to")
    val commitTo: String? = null,
    @JsonProperty("ref")
    val ref: String? = null,
    @JsonProperty("ref_count")
    val refCount: Any? = null,
    @JsonProperty("ref_type")
    val refType: String? = null
)
