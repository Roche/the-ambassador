package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.Api
import com.filipowm.gitlab.api.client.GitLabHttpClient
import com.filipowm.gitlab.api.project.model.Contributor
import com.filipowm.gitlab.api.utils.Sort
import java.util.stream.Stream

class RepositoryApi(basePath: String, client: GitLabHttpClient) : Api(basePath, client) {

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
