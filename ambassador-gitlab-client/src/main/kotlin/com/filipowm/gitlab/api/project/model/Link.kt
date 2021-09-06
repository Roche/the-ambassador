package com.filipowm.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Link(
    @JsonProperty("external")
    val `external`: Boolean? = null,
    @JsonProperty("id")
    val id: Int? = null,
    @JsonProperty("link_type")
    val linkType: String? = null,
    @JsonProperty("name")
    val name: String? = null,
    @JsonProperty("url")
    val url: String? = null
)
