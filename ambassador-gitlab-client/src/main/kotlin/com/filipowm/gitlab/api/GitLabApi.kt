package com.filipowm.gitlab.api

import com.filipowm.gitlab.api.client.GitLabHttpClient
import com.filipowm.gitlab.api.groups.GroupsApi
import com.filipowm.gitlab.api.project.ProjectsApi

internal class GitLabApi(private val url: String, basePath: String, httpClient: GitLabHttpClient) : Api(basePath, httpClient), GitLab {
    override fun url(): String = url

    override fun projects() = ProjectsApi("$basePath/projects", client)

    override fun groups() = GroupsApi("$basePath/groups", client)

    override fun issueStatistics() = IssueStatisticsApi("$basePath/issues_statistics", client)
}
