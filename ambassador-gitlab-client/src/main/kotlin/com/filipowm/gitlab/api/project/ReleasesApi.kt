package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.Api
import com.filipowm.gitlab.api.project.model.Release
import com.filipowm.gitlab.api.utils.Pager
import com.filipowm.gitlab.api.utils.Pagination
import com.filipowm.gitlab.api.utils.Sort
import io.ktor.client.*
import java.util.stream.Stream

class ReleasesApi(basePath: String, client: HttpClient) : Api(basePath, client) {

    suspend fun stream(sort: Sort = Sort.none(), fromPagination: Pagination = Pagination()): Stream<Release> {
        return paging(sort, fromPagination).stream()
    }

    suspend fun list(sort: Sort = Sort.none(), pagination: Pagination = Pagination()): List<Release> {
        return paging(sort, pagination).next().content
    }

    suspend fun paging(
        sort: Sort = Sort.none(),
        fromPagination: Pagination = Pagination()
    ): Pager<Release> = Pager(fromPagination) { doGetPage(it, sort) }

}