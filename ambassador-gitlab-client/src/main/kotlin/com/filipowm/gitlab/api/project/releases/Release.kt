package com.filipowm.gitlab.api.project.releases

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.filipowm.gitlab.api.project.repository.Commit
import com.filipowm.gitlab.api.project.mergerequests.Milestone
import com.filipowm.gitlab.api.project.model.SimpleUser
import com.filipowm.gitlab.api.utils.Dates
import java.time.LocalDateTime

data class Release(
    @JsonProperty("assets")
    val assets: Assets? = null,
    @JsonProperty("author")
    val author: SimpleUser? = null,
    @JsonProperty("commit")
    val commit: Commit? = null,
    @JsonProperty("commit_path")
    val commitPath: String? = null,
    @JsonProperty("created_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val createdAt: LocalDateTime? = null,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("evidences")
    val evidences: List<Evidence>? = listOf(),
    @JsonProperty("milestones")
    val milestones: List<Milestone>? = listOf(),
    @JsonProperty("name")
    val name: String? = null,
    @JsonProperty("released_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val releasedAt: LocalDateTime? = null,
    @JsonProperty("tag_name")
    val tagName: String? = null,
    @JsonProperty("tag_path")
    val tagPath: String? = null
)
