package com.roche.gitlab.api.groups

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.project.model.Project
import com.roche.gitlab.api.utils.PageProvider
import com.roche.gitlab.api.utils.Pager
import com.roche.gitlab.api.utils.Pagination

class GroupApi internal constructor(basePath: String, client: GitLabHttpClient) : Api(basePath, client) {

    fun projects(
        groupProjectListQuery: GroupProjectListQuery = GroupProjectListQuery(),
        pagination: Pagination = Pagination()
    ): Pager<Project> {
        val pageProvider: PageProvider<Project> = {
            doGetPage("projects", it, groupProjectListQuery)
        }
        return Pager(pagination, pageProvider)
    }
}