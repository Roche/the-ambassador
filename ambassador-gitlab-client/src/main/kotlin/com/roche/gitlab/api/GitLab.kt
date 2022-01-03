package com.roche.gitlab.api

import com.roche.gitlab.api.groups.GroupsApi
import com.roche.gitlab.api.project.ProjectsApi
import com.roche.gitlab.api.user.MeApi

interface GitLab {

    fun url(): String
    fun projects(): ProjectsApi
    fun groups(): GroupsApi
    fun issueStatistics(): IssueStatisticsApi
    fun me(): MeApi

    companion object {
        fun builder(): GitLabApiBuilder {
            return GitLabApiBuilder()
        }
    }

    enum class ApiVersion {
        V4
    }
}
