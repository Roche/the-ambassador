package com.roche.gitlab.api.project.issues

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.gitlab.api.project.model.SimpleUser
import com.roche.gitlab.api.utils.Dates
import java.time.LocalDate
import java.time.LocalDateTime

data class Issue(
    @JsonProperty("assignee")
    val assignee: SimpleUser? = null,
    @JsonProperty("assignees")
    val assignees: List<SimpleUser> = listOf(),
    @JsonProperty("author")
    val author: SimpleUser? = null,
    @JsonProperty("closed_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val closedAt: LocalDateTime? = null,
    @JsonProperty("closed_by")
    val closedBy: SimpleUser? = null,
    @JsonProperty("confidential")
    val confidential: Boolean? = null,
    @JsonProperty("created_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val createdAt: LocalDateTime,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("discussion_locked")
    val discussionLocked: Boolean? = null,
    @JsonProperty("downvotes")
    val downvotes: Int? = null,
    @JsonProperty("due_date")
    val dueDate: LocalDate? = null,
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("iid")
    val iid: Long,
    @JsonProperty("issue_type")
    val issueType: String? = null,
    @JsonProperty("labels")
    val labels: List<String> = listOf(),
    @JsonProperty("_links")
    val links: Links? = null,
    @JsonProperty("merge_requests_count")
    val mergeRequestsCount: Int? = null,
//    @JsonProperty("milestone")
//    val milestone: Any? = null,
    @JsonProperty("project_id")
    val projectId: Long,
    @JsonProperty("references")
    val references: References? = null,
    @JsonProperty("state")
    val state: State,
    @JsonProperty("service_desk_reply_to")
    val serviceDeskReplyTo: String? = null,
    @JsonProperty("subscribed")
    val subscribed: Boolean = false,
    @JsonProperty("task_completion_status")
    val taskCompletionStatus: TaskCompletionStatus? = null,
    @JsonProperty("time_stats")
    val timeStats: TimeStats? = null,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("type")
    val type: String? = null,
    @JsonProperty("updated_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val updatedAt: LocalDateTime? = null,
    @JsonProperty("upvotes")
    val upvotes: Int? = null,
    @JsonProperty("user_notes_count")
    val userNotesCount: Int? = null,
    @JsonProperty("web_url")
    val webUrl: String? = null
) {

    enum class State {
        OPENED,
        CLOSED,
        REOPENED
    }
}