package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.Api
import com.filipowm.gitlab.api.project.model.RepositoryFile
import io.ktor.client.*
import java.util.*

class RepositoryFilesApi(basePath: String, client: HttpClient) : Api(basePath, client) {

    suspend fun get(path: String, ref: String): Optional<RepositoryFile> = doGetOptional(path, params = mapOf("ref" to ref))

}