package com.roche.gitlab.api.groups

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.utils.PageProvider
import com.roche.gitlab.api.utils.Pager
import com.roche.gitlab.api.utils.Pagination

class GroupsApi(basePath: String, client: GitLabHttpClient) : Api(basePath, client) {

    fun withId(id: Long): GroupApi {
        return withPath(id.toString())
    }

    fun withPath(path: String): GroupApi {
        return GroupApi("$basePath/$path", client)
    }

    fun paging(query: GroupsListQuery, pagination: Pagination): Pager<Group> {
        val pageProvider: PageProvider<Group> = {
            doGetPage(it, query)
        }
        return Pager(pagination, pageProvider)
    }
}
