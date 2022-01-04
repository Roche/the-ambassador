package com.roche.gitlab.api.project.events

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.gitlab.api.utils.Dates
import java.time.LocalDateTime

data class Event(
    @JsonProperty("action_name")
    val action: Action? = null,
    @JsonProperty("author")
    val author: Author? = null,
    @JsonProperty("author_id")
    val authorId: Long? = null,
    @JsonProperty("author_username")
    val authorUsername: String? = null,
    @JsonProperty("created_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val createdAt: LocalDateTime? = null,
    @JsonProperty("id")
    val id: Long? = null,
    @JsonProperty("note")
    val note: Note? = null,
    @JsonProperty("project_id")
    val projectId: Int? = null,
    @JsonProperty("push_data")
    val pushData: PushData? = null,
    @JsonProperty("target_id")
    val targetId: Long? = null,
    @JsonProperty("target_iid")
    val targetIid: Long? = null,
    @JsonProperty("target_title")
    val targetTitle: String? = null,
    @JsonProperty("target_type")
    val targetType: TargetType? = null
)
