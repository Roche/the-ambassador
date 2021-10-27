package com.roche.gitlab.api.project.mergerequests

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.gitlab.api.utils.Dates
import java.time.LocalDate
import java.time.LocalDateTime

data class Milestone(
    @JsonProperty("created_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val createdAt: LocalDateTime? = null,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("due_date")
    val dueDate: LocalDate? = null,
    @JsonProperty("id")
    val id: Int? = null,
    @JsonProperty("iid")
    val iid: Int? = null,
    @JsonProperty("issue_stats")
    val issueStats: IssueStats? = null,
    @JsonProperty("project_id")
    val projectId: Int? = null,
    @JsonProperty("start_date")
    val startDate: LocalDate? = null,
    @JsonProperty("state")
    val state: State? = null,
    @JsonProperty("title")
    val title: String? = null,
    @JsonProperty("updated_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val updatedAt: LocalDateTime? = null,
    @JsonProperty("web_url")
    val webUrl: String? = null
) {

    enum class State {
        CLOSED,
        ACTIVE
    }
}
