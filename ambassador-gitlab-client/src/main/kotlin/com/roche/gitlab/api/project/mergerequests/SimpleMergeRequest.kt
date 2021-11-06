package com.roche.gitlab.api.project.mergerequests

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.gitlab.api.utils.Dates
import java.time.LocalDateTime

data class SimpleMergeRequest(
    @JsonProperty("created_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val createdAt: LocalDateTime? = null,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("id")
    val id: Long? = null,
    @JsonProperty("iid")
    val iid: Long? = null,
    @JsonProperty("project_id")
    val projectId: Long? = null,
    @JsonProperty("state")
    val state: MergeRequest.State? = null,
    @JsonProperty("title")
    val title: String? = null,
    @JsonProperty("updated_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val updatedAt: LocalDateTime? = null,
    @JsonProperty("web_url")
    val webUrl: String? = null
)
