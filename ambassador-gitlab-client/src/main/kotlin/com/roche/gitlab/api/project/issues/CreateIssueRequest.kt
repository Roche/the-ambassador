package com.roche.gitlab.api.project.issues

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
open class CreateIssueRequest(
    @JsonIgnore
    val projectId: Long,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("labels")
    val labels: List<String>? = listOf(),
    @JsonProperty("assignee_id")
    val assigneeId: Long? = null,
    @JsonProperty("confidential")
    val confidential: Boolean = false,
    @JsonProperty("due_date")
    val dueDate: LocalDate? = null,
    @JsonProperty("issue_type")
    val issueType: IssueType = IssueType.ISSUE,
    @JsonProperty("weight")
    val weight: Int? = null,
    @JsonProperty("milestone_id")
    val milestoneId: Int? = null,
    @JsonProperty("epic_id")
    val epicId: Int? = null,
)