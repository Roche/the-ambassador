package com.roche.gitlab.api.project.repository

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.client.GitLabHttpClient
import java.util.*

class RepositoryFilesApi(basePath: String, client: GitLabHttpClient) : Api(basePath, client) {

    suspend fun get(path: String, ref: String): Optional<RepositoryFile> = doGetOptional(path, params = mapOf("ref" to ref))
}
