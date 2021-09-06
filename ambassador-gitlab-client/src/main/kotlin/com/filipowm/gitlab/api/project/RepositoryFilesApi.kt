package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.Api
import com.filipowm.gitlab.api.client.GitLabHttpClient
import com.filipowm.gitlab.api.project.model.RepositoryFile
import java.util.*

class RepositoryFilesApi(basePath: String, client: GitLabHttpClient) : Api(basePath, client) {

    suspend fun get(path: String, ref: String): Optional<RepositoryFile> = doGetOptional(path, params = mapOf("ref" to ref))
}
