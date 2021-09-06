package com.filipowm.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonProperty

data class License(
    @JsonProperty("html_url")
    var htmlUrl: String?,
    @JsonProperty("key")
    var key: String?,
    @JsonProperty("name")
    var name: String?,
    @JsonProperty("nickname")
    var nickname: String?,
    @JsonProperty("source_url")
    var sourceUrl: String?
)
