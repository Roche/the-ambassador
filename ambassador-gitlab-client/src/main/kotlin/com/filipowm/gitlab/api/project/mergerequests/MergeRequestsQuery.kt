package com.filipowm.gitlab.api.project.mergerequests

import com.filipowm.gitlab.api.model.YesNo
import com.filipowm.gitlab.api.utils.QueryParam
import java.time.LocalDateTime

data class MergeRequestsQuery(
    @QueryParam("state") val state: String? = null,
    @QueryParam("milestone") val milestone: String? = null,
    @QueryParam("view") var view: View? = null,
    @QueryParam("labels") val labels: String? = null,
    @QueryParam("with_labels_details") val withLabelsDetails: Boolean? = null,
    @QueryParam("with_merge_status_recheck") val withMergeStatusRecheck: Boolean? = null,
    @QueryParam("created_after") val createdAfter: LocalDateTime? = null,
    @QueryParam("created_before") val createdBefore: LocalDateTime? = null,
    @QueryParam("updated_after") val updatedAfter: LocalDateTime? = null,
    @QueryParam("updated_before") val updatedBefore: LocalDateTime? = null,
    @QueryParam("scope") val scope: Scope? = null,
    @QueryParam("author_id") val authorId: Long? = null,
    @QueryParam("author_username") val authorUsername: String? = null,
    @QueryParam("assignee_id") val assigneeId: Long? = null,
    @QueryParam("approver_ids") val approverIds: List<Long>? = null,
    @QueryParam("approved_by_ids") val approvedByIds: List<Long>? = null,
    @QueryParam("reviewer_id") val reviewerId: Long? = null,
    @QueryParam("reviewer_username") val reviewerUsername: String? = null,
    @QueryParam("my_reaction_emoji") val myReactionEmoji: String? = null,
    @QueryParam("source_branch") val sourceBranch: String? = null,
    @QueryParam("target_branch") val targetBranch: String? = null,
    @QueryParam("search") val search: String? = null,
    @QueryParam("in") val searchIn: String? = null,
    @QueryParam("wip") val wip: YesNo? = null,
    @QueryParam("not") val not: String? = null,
    @QueryParam("environment") val environment: String? = null,
    @QueryParam("deployed_before") val deployedBefore: LocalDateTime? = null,
    @QueryParam("deployed_after") val deployedAfter: LocalDateTime? = null,
) {
    enum class View {
        SIMPLE
    }

    enum class Scope {
        CREATED_BY_ME,
        ASSIGNED_TO_ME,
        ALL
    }
}