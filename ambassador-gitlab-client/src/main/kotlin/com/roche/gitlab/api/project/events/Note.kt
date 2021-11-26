package com.roche.gitlab.api.project.events


import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.gitlab.api.utils.Dates
import java.time.LocalDateTime

data class Note(
    @JsonProperty("attachment")
    val attachment: Any? = null,
    @JsonProperty("author")
    val author: Author? = null,
    @JsonProperty("body")
    val body: String? = null,
    @JsonProperty("confidential")
    val confidential: Boolean? = null,
    @JsonProperty("created_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val createdAt: LocalDateTime,
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("noteable_id")
    val noteableId: Long? = null,
    @JsonProperty("noteable_iid")
    val noteableIid: Long? = null,
    @JsonProperty("noteable_type")
    val noteableType: TargetType? = null,
    @JsonProperty("resolvable")
    val resolvable: Boolean? = null,
    @JsonProperty("resolved")
    val resolved: Boolean? = null,
    @JsonProperty("resolved_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val resolvedAt: LocalDateTime? = null,
    @JsonProperty("resolved_by")
    val resolvedBy: ResolvedBy? = null,
    @JsonProperty("system")
    val system: Boolean? = null,
    @JsonProperty("type")
    val type: TargetType? = null,
    @JsonProperty("updated_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val updatedAt: LocalDateTime? = null
)