package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.Api
import com.filipowm.gitlab.api.client.GitLabHttpClient
import com.filipowm.gitlab.api.project.model.Member
import com.filipowm.gitlab.api.utils.Pager
import com.filipowm.gitlab.api.utils.Pagination

class MembersApi internal constructor(basePath: String, httpClient: GitLabHttpClient) : Api(basePath, httpClient) {

    suspend fun list(pagination: Pagination = Pagination()): List<Member> {
        return paging(pagination).get()
    }

    suspend fun paging(fromPagination: Pagination = Pagination()): Pager<Member> = Pager(fromPagination) { doGetPage(it) }

}