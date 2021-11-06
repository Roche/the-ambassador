package com.roche.gitlab.api.project.pipelines

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.gitlab.api.utils.Dates
import java.time.LocalDateTime

data class SimplePipeline(
    @JsonProperty("created_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val createdAt: LocalDateTime,
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("iid")
    val iid: Long? = null,
    @JsonProperty("project_id")
    val projectId: Long,
    @JsonProperty("ref")
    val ref: String,
    @JsonProperty("sha")
    val sha: String,
    @JsonProperty("source")
    val source: String? = null,
    @JsonProperty("status")
    val status: Status,
    @JsonProperty("updated_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val updatedAt: LocalDateTime? = null,
    @JsonProperty("web_url")
    val webUrl: String? = null
) {
    enum class Status {
        CREATED,
        WAITING_FOR_RESOURCE,
        PREPARING,
        PENDING,
        RUNNING,
        SUCCESS,
        FAILED,
        CANCELED,
        SKIPPED,
        MANUAL,
        SCHEDULED
    }

    enum class Scope {
        RUNNING,
        PENDING,
        FINISHED,
        BRANCHES,
        TAGS
    }
}
