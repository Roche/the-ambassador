package com.filipowm.gitlab.api.model

import com.fasterxml.jackson.annotation.JsonProperty

data class AccessLevel(
    @JsonProperty("access_level")
    val accessLevel: AccessLevelName,
    @JsonProperty("access_level_description")
    val accessLevelDescription: String? = null
)