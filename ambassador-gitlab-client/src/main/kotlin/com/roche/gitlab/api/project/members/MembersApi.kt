package com.roche.gitlab.api.project.members

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.utils.Pager
import com.roche.gitlab.api.utils.Pagination

class MembersApi internal constructor(basePath: String, httpClient: GitLabHttpClient) : Api(basePath, httpClient) {

    suspend fun list(pagination: Pagination = Pagination()): List<Member> {
        return paging(pagination).get()
    }

    suspend fun paging(fromPagination: Pagination = Pagination()): Pager<Member> = Pager(fromPagination) { doGetPage(it) }
}
