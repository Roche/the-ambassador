package com.filipowm.gitlab.api.project.mergerequests


import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.filipowm.gitlab.api.project.model.*
import com.filipowm.gitlab.api.utils.Dates
import java.time.LocalDateTime

data class MergeRequest(
    @JsonProperty("allow_collaboration")
    val allowCollaboration: Boolean? = null,
    @JsonProperty("allow_maintainer_to_push")
    val allowMaintainerToPush: Boolean? = null,
    @JsonProperty("assignee")
    val assignee: SimpleUser? = null,
    @JsonProperty("assignees")
    val assignees: List<SimpleUser>? = null,
    @JsonProperty("author")
    val author: SimpleUser? = null,
    @JsonProperty("blocking_discussions_resolved")
    val blockingDiscussionsResolved: Boolean? = null,
    @JsonProperty("closed_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val closedAt: LocalDateTime? = null,
    @JsonProperty("closed_by")
    val closedBy: String? = null,
    @JsonProperty("created_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val createdAt: LocalDateTime? = null,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("discussion_locked")
    val discussionLocked: Boolean? = null,
    @JsonProperty("downvotes")
    val downvotes: Int? = null,
    @JsonProperty("draft")
    val draft: Boolean? = null,
    @JsonProperty("force_remove_source_branch")
    val forceRemoveSourceBranch: Boolean? = null,
    @JsonProperty("has_conflicts")
    val hasConflicts: Boolean? = null,
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("iid")
    val iid: Long,
    @JsonProperty("labels")
    val labels: List<String>? = null,
    @JsonProperty("merge_commit_sha")
    val mergeCommitSha: String? = null,
    @JsonProperty("merge_status")
    val mergeStatus: MergeStatus? = null,
    @JsonProperty("merge_when_pipeline_succeeds")
    val mergeWhenPipelineSucceeds: Boolean? = null,
    @JsonProperty("merged_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val mergedAt: LocalDateTime? = null,
    @JsonProperty("merged_by")
    val mergedBy: SimpleUser? = null,
    @JsonProperty("milestone")
    val milestone: Milestone? = null,
    @JsonProperty("project_id")
    val projectId: Int? = null,
    @JsonProperty("references")
    val references: References? = null,
    @JsonProperty("reviewers")
    val reviewers: List<SimpleUser>? = null,
    @JsonProperty("sha")
    val sha: String? = null,
    @JsonProperty("should_remove_source_branch")
    val shouldRemoveSourceBranch: Boolean? = null,
    @JsonProperty("source_branch")
    val sourceBranch: String? = null,
    @JsonProperty("source_project_id")
    val sourceProjectId: Long? = null,
    @JsonProperty("squash")
    val squash: Boolean? = null,
    @JsonProperty("squash_commit_sha")
    val squashCommitSha: String? = null,
    @JsonProperty("state")
    val state: State? = null,
    @JsonProperty("target_branch")
    val targetBranch: String? = null,
    @JsonProperty("target_project_id")
    val targetProjectId: Int? = null,
    @JsonProperty("task_completion_status")
    val taskCompletionStatus: TaskCompletionStatus? = null,
    @JsonProperty("time_stats")
    val timeStats: TimeStats? = null,
    @JsonProperty("title")
    val title: String? = null,
    @JsonProperty("updated_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val updatedAt: LocalDateTime? = null,
    @JsonProperty("upvotes")
    val upvotes: Int? = null,
    @JsonProperty("user_notes_count")
    val userNotesCount: Int? = null,
    @JsonProperty("web_url")
    val webUrl: String? = null,
    @JsonProperty("work_in_progress")
    val workInProgress: Boolean? = null
) {
    enum class State {

        OPENED,
        CLOSED,
        LOCKED,
        MERGED

    }
}