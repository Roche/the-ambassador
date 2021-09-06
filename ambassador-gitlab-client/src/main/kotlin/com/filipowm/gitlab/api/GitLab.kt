package com.filipowm.gitlab.api

import com.filipowm.gitlab.api.groups.GroupsApi
import com.filipowm.gitlab.api.project.ProjectsApi

interface GitLab {

    fun projects(): ProjectsApi
    fun groups(): GroupsApi
    fun issueStatistics(): IssueStatisticsApi

    companion object {
        fun builder(): GitLabApiBuilder {
            return GitLabApiBuilder()
        }
    }

    enum class ApiVersion {
        V4
    }
}
