package com.filipowm.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.filipowm.gitlab.api.model.UserState

data class SimpleUser(
    @JsonProperty("avatar_url")
    val avatarUrl: String? = null,
    @JsonProperty("id")
    val id: Int? = null,
    @JsonProperty("name")
    val name: String? = null,
    @JsonProperty("state")
    val state: UserState? = null,
    @JsonProperty("username")
    val username: String? = null,
    @JsonProperty("web_url")
    val webUrl: String? = null
)
