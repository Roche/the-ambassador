package com.roche.gitlab.api.project.mergerequests

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.utils.Pager
import com.roche.gitlab.api.utils.Pagination

class MergeRequestsApi internal constructor(basePath: String, client: GitLabHttpClient) : Api(basePath, client) {

    suspend fun list(
        mergeRequestsQuery: MergeRequestsQuery = MergeRequestsQuery(),
        pagination: Pagination = Pagination()
    ): List<MergeRequest> {
        return paging(mergeRequestsQuery, pagination).get()
    }

    suspend fun paging(
        mergeRequestsQuery: MergeRequestsQuery = MergeRequestsQuery(),
        fromPagination: Pagination = Pagination()
    ): Pager<MergeRequest> = Pager(fromPagination) { doGetPage(it, mergeRequestsQuery) }

    suspend fun simpleList(
        mergeRequestsQuery: MergeRequestsQuery = MergeRequestsQuery(),
        pagination: Pagination = Pagination()
    ): List<SimpleMergeRequest> {
        return simplePaging(mergeRequestsQuery, pagination).get()
    }

    suspend fun simplePaging(
        mergeRequestsQuery: MergeRequestsQuery = MergeRequestsQuery(),
        fromPagination: Pagination = Pagination()
    ): Pager<SimpleMergeRequest> = Pager(fromPagination) {
        mergeRequestsQuery.view = MergeRequestsQuery.View.SIMPLE
        doGetPage(it, mergeRequestsQuery)
    }

}