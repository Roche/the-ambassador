package com.roche.gitlab.api.project.issues

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

class UpdateIssueRequest(
    projectId: Long,
    @JsonIgnore
    val issueId: Long,
    title: String,
    description: String? = null,
    @JsonProperty("add_labels")
    val addLabels: List<String> = listOf(),
    @JsonProperty("remove_labels")
    val removeLabels: List<String> = listOf(),
    labels: List<String>? = null, // purposely nullable, cause ending empty would unassign all existing labels.
    @JsonProperty("discussion_locked")
    val discussionLocked: Boolean? = null,
    @JsonProperty("assignee_id")
    assigneeId: Long? = null,
    confidential: Boolean = false,
    @JsonProperty("due_date")
    dueDate: LocalDate? = null,
    @JsonProperty("issue_type")
    issueType: IssueType = IssueType.ISSUE,
    weight: Int? = null,
    @JsonProperty("milestone_id")
    milestoneId: Int? = null,
    @JsonProperty("epic_id")
    epicId: Int? = null,
    @JsonProperty("state_event")
    val state: IssueStateEvent? = null, //
) : CreateIssueRequest(projectId, title, description, labels, assigneeId, confidential, dueDate, issueType, weight, milestoneId, epicId) {
    enum class IssueStateEvent {
        @JsonProperty("close")
        CLOSE,
        @JsonProperty("reopen")
        REOPEN
    }
}