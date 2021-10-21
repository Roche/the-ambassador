package com.filipowm.gitlab.api.project.branches

import com.filipowm.gitlab.api.Api
import com.filipowm.gitlab.api.client.GitLabHttpClient
import java.util.*
import java.util.stream.Stream

class ProtectedBranchesApi internal constructor(basePath: String, client: GitLabHttpClient) : Api(basePath, client) {

    suspend fun get(name: String): Optional<ProtectedBranch> = doGet(name)

    suspend fun list(): List<ProtectedBranch> {
        return doGetList()
    }

    suspend fun stream(): Stream<ProtectedBranch> {
        return doGetStream()
    }
}
