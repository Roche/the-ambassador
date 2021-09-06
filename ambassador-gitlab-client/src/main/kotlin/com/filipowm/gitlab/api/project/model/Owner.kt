package com.filipowm.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Owner(
    @JsonProperty("created_at")
    var createdAt: String?,
    @JsonProperty("id")
    var id: Int,
    @JsonProperty("name")
    var name: String
)
