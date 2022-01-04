package com.roche.ambassador.gitlab

import com.roche.ambassador.model.source.Issue
import com.roche.gitlab.api.project.issues.CreateIssueRequest
import com.roche.gitlab.api.project.issues.UpdateIssueRequest
import com.roche.gitlab.api.project.issues.Issue as GitLabIssue

internal object IssueMapper {

    fun fromGitLab(gitlabIssue: GitLabIssue): Issue {
        return Issue(
            gitlabIssue.iid, gitlabIssue.projectId,
            gitlabIssue.title, gitlabIssue.description ?: "",
            gitlabIssue.labels, Issue.Status.OPEN
        )
    }

    fun toCreateRequest(issue: Issue): CreateIssueRequest {
        return CreateIssueRequest(issue.projectId, issue.title, issue.description, issue.labels)
    }

    fun toUpdateRequest(issue: Issue): UpdateIssueRequest {
        return UpdateIssueRequest(issue.projectId, issue.getId()!!, issue.title, issue.description, labels = issue.labels, state = getIssueStateEvent(issue.status))
    }

    private fun getIssueStateEvent(status: Issue.Status): UpdateIssueRequest.IssueStateEvent? {
        return when (status) {
            Issue.Status.CLOSED -> UpdateIssueRequest.IssueStateEvent.CLOSE
            Issue.Status.REOPENED -> UpdateIssueRequest.IssueStateEvent.REOPEN
            else -> null // no need for reopening already open issue
        }
    }
}
