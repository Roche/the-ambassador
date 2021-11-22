package com.roche.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Namespace(
    @JsonProperty("avatar_url")
    var avatarUrl: String? = null,
    @JsonProperty("full_path")
    var fullPath: String? = null,
    @JsonProperty("id")
    var id: Long? = null,
    @JsonProperty("kind")
    var kind: NamespaceKind? = null,
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("path")
    var path: String? = null,
    @JsonProperty("web_url")
    var webUrl: String? = null
)
