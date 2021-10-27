package com.roche.gitlab.api.project.repository

import com.fasterxml.jackson.annotation.JsonProperty

data class Contributor(
    @JsonProperty("additions")
    val additions: Int,
    @JsonProperty("commits")
    val commits: Int,
    @JsonProperty("deletions")
    val deletions: Int,
    @JsonProperty("email")
    val email: String,
    @JsonProperty("name")
    val name: String
)
