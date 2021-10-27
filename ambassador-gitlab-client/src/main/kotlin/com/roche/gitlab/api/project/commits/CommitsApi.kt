package com.roche.gitlab.api.project.commits

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.project.repository.Commit
import com.roche.gitlab.api.utils.Pager
import com.roche.gitlab.api.utils.Pagination

class CommitsApi internal constructor(basePath: String, client: GitLabHttpClient) : Api(basePath, client) {

    suspend fun list(query: CommitsQuery, pagination: Pagination = Pagination()): List<Commit> {
        return paging(query, pagination).get()
    }

    suspend fun paging(
        query: CommitsQuery = CommitsQuery(),
        fromPagination: Pagination = Pagination()
    ): Pager<Commit> = Pager(fromPagination) { doGetPage(it, query) }
}
