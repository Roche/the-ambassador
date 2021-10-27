package com.roche.gitlab.api.project.releases

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.utils.Pager
import com.roche.gitlab.api.utils.Pagination
import com.roche.gitlab.api.utils.Sort

class ReleasesApi(basePath: String, client: GitLabHttpClient) : Api(basePath, client) {

    suspend fun list(sort: Sort = Sort.none(), pagination: Pagination = Pagination()): List<Release> {
        return paging(sort, pagination).get()
    }

    suspend fun paging(
        sort: Sort = Sort.none(),
        fromPagination: Pagination = Pagination()
    ): Pager<Release> = Pager(fromPagination) { doGetPage(it, sort) }
}
