package com.roche.ambassador.gitlab

import com.roche.ambassador.model.source.Issue
import com.roche.ambassador.model.source.IssuesManager
import com.roche.gitlab.api.GitLab
import com.roche.gitlab.api.project.issues.IssuesApi
import java.util.*
import com.roche.gitlab.api.project.issues.Issue as GitLabIssue

class GitLabIssuesManager(private val gitlab: GitLab) : IssuesManager {
    override suspend fun get(id: Long, projectId: Long): Optional<Issue> {
        return api(projectId).get(id).map { IssueMapper.fromGitLab(it) }
    }

    override suspend fun create(issue: Issue): Issue {
        val createRequest = IssueMapper.toCreateRequest(issue)
        return issue.api()
            .create(createRequest)
            .mapToAmbassador()
    }

    override suspend fun update(issue: Issue): Issue {
        val updateRequest = IssueMapper.toUpdateRequest(issue)
        return issue.api()
            .update(updateRequest)
            .mapToAmbassador()
    }

    private fun api(projectId: Long): IssuesApi {
        return gitlab.projects()
            .withId(projectId)
            .issues()
    }

    private fun Issue.api(): IssuesApi = api(projectId)

    private fun Optional<GitLabIssue>.mapToAmbassador(): Issue = map { IssueMapper.fromGitLab(it) }
        .orElseThrow() // TODO throw custom exception
}