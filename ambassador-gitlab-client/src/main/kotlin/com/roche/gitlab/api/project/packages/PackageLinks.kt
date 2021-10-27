package com.roche.gitlab.api.project.packages


import com.fasterxml.jackson.annotation.JsonProperty

data class PackageLinks(
    @JsonProperty("delete_api_path")
    val deleteApiPath: String? = null,
    @JsonProperty("web_path")
    val webPath: String? = null
)