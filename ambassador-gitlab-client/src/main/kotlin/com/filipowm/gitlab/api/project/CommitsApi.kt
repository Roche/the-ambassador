package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.Api
import com.filipowm.gitlab.api.project.model.Commit
import com.filipowm.gitlab.api.utils.Pager
import com.filipowm.gitlab.api.utils.Pagination
import io.ktor.client.*

class CommitsApi internal constructor(basePath: String, client: HttpClient) : Api(basePath, client) {

    suspend fun list(query: CommitsQuery, pagination: Pagination = Pagination()): List<Commit> {
        return paging(query, pagination).next().content
    }

    suspend fun paging(
        query: CommitsQuery = CommitsQuery(),
        fromPagination: Pagination = Pagination()
    ): Pager<Commit> {
        return Pager(fromPagination) { doGetPage(it, query) }
    }

}