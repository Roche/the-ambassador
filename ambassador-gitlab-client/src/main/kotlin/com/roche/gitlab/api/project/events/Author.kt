package com.roche.gitlab.api.project.events

import com.fasterxml.jackson.annotation.JsonProperty

data class Author(
    @JsonProperty("avatar_url")
    val avatarUrl: String? = null,
    @JsonProperty("id")
    val id: Int? = null,
    @JsonProperty("name")
    val name: String? = null,
    @JsonProperty("state")
    val state: String? = null,
    @JsonProperty("username")
    val username: String? = null,
    @JsonProperty("web_url")
    val webUrl: String? = null
)
