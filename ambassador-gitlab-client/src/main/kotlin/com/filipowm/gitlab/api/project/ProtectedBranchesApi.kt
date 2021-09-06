package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.Api
import io.ktor.client.*
import java.util.*
import java.util.stream.Stream

class ProtectedBranchesApi internal constructor(basePath: String, client: HttpClient) : Api(basePath, client) {

    suspend fun get(name: String): Optional<ProtectedBranch> = doGet(name)

    suspend fun list(): List<ProtectedBranch> {
        return doGetList()
    }

    suspend fun stream(): Stream<ProtectedBranch> {
        return doGetStream()
    }
}