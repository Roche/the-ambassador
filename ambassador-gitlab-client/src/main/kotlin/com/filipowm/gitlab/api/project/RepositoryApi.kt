package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.Api
import com.filipowm.gitlab.api.utils.Sort
import io.ktor.client.*
import java.util.stream.Stream

class RepositoryApi(basePath: String, client: HttpClient) : Api(basePath, client) {

    suspend fun getContributors(sort: Sort = Sort.none()): List<Contributor> {
        return doGetList(path = "contributors", sort)
    }

    suspend fun getContributorsStream(sort: Sort = Sort.none()): Stream<Contributor> {
        return getContributors(sort).stream()
    }

    fun files(): RepositoryFilesApi {
        return RepositoryFilesApi("$basePath/files", client)
    }

    fun commits(): CommitsApi {
        return CommitsApi("$basePath/commits", client)
    }
}