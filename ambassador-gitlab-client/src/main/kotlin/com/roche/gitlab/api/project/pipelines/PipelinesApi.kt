package com.roche.gitlab.api.project.pipelines

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.utils.Pager
import com.roche.gitlab.api.utils.Pagination
import com.roche.gitlab.api.utils.Sort

class PipelinesApi internal constructor(basePath: String, httpClient: GitLabHttpClient) : Api(basePath, httpClient) {

    suspend fun list(
        pipelinesQuery: PipelinesQuery = PipelinesQuery(),
        fromPagination: Pagination = Pagination(),
        sort: Sort = Sort.none()
    ): List<SimplePipeline> {
        return paging(pipelinesQuery, fromPagination, sort).get()
    }

    suspend fun paging(
        pipelinesQuery: PipelinesQuery = PipelinesQuery(),
        fromPagination: Pagination = Pagination(),
        sort: Sort = Sort.none()
    ): Pager<SimplePipeline> = Pager(fromPagination) { doGetPage(it, pipelinesQuery, sort) }

}