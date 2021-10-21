package com.filipowm.gitlab.api.project.repository

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.filipowm.gitlab.api.utils.Dates
import java.time.LocalDateTime

data class Commit(
    @JsonProperty("author_email")
    val authorEmail: String,
    @JsonProperty("author_name")
    val authorName: String,
    @JsonProperty("authored_date")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val authoredDate: LocalDateTime,
    @JsonProperty("committed_date")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val committedDate: LocalDateTime,
    @JsonProperty("committer_email")
    val committerEmail: String,
    @JsonProperty("committer_name")
    val committerName: String,
    @JsonProperty("created_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val createdAt: LocalDateTime,
    @JsonProperty("id")
    val id: String,
    @JsonProperty("message")
    val message: String? = null,
    @JsonProperty("parent_ids")
    val parentIds: List<String> = listOf(),
    @JsonProperty("short_id")
    val shortId: String,
    @JsonProperty("title")
    val title: String? = null,
    @JsonProperty("web_url")
    val webUrl: String? = null,
    @JsonProperty("stats")
    val stats: CommitStats? = null
)
