package com.roche.gitlab.api

import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.groups.GroupsApi
import com.roche.gitlab.api.project.ProjectsApi
import com.roche.gitlab.api.user.MeApi

internal class GitLabApi(private val url: String, basePath: String, httpClient: GitLabHttpClient) : Api(basePath, httpClient), GitLab {
    override fun url(): String = url

    override fun projects() = ProjectsApi("$basePath/projects", client)

    override fun groups() = GroupsApi("$basePath/groups", client)

    override fun issueStatistics() = IssueStatisticsApi("$basePath/issues_statistics", client)

    override fun me(): MeApi = MeApi("$basePath/user", client)
}
