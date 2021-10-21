package com.filipowm.gitlab.api.project.releases

import com.fasterxml.jackson.annotation.JsonProperty

data class Assets(
    @JsonProperty("count")
    val count: Int? = null,
    @JsonProperty("evidence_file_path")
    val evidenceFilePath: String? = null,
    @JsonProperty("links")
    val links: List<Link> = listOf(),
    @JsonProperty("sources")
    val sources: List<Source> = listOf()
)
