package com.roche.gitlab.api.groups

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.project.ProjectApi
import com.roche.gitlab.api.project.model.Project
import com.roche.gitlab.api.utils.PageProvider
import com.roche.gitlab.api.utils.Pager

class GroupsApi(basePath: String, client: GitLabHttpClient) : Api(basePath, client) {

    fun withId(id: Long): GroupApi {
        return withPath(id.toString())
    }

    fun withPath(path: String): GroupApi {
        return GroupApi("$basePath/$path", client)
    }

}
