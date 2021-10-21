package com.filipowm.gitlab.api.project.packages

import com.filipowm.gitlab.api.Api
import com.filipowm.gitlab.api.client.GitLabHttpClient
import com.filipowm.gitlab.api.utils.Pager
import com.filipowm.gitlab.api.utils.Pagination
import com.filipowm.gitlab.api.utils.Sort

class PackagesApi internal constructor(basePath: String, httpClient: GitLabHttpClient) : Api(basePath, httpClient) {

    suspend fun list(
        packagesQuery: PackagesQuery = PackagesQuery(),
        fromPagination: Pagination = Pagination(),
        sort: Sort = Sort.none()
    ): List<SimplePackage> {
        return paging(packagesQuery, fromPagination, sort).get()
    }

    suspend fun paging(
        packagesQuery: PackagesQuery = PackagesQuery(),
        fromPagination: Pagination = Pagination(),
        sort: Sort = Sort.none()
    ): Pager<SimplePackage> = Pager(fromPagination) { doGetPage(it, packagesQuery, sort) }

}