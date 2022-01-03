package com.roche.gitlab.api.user

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.client.GitLabHttpClient

class MeApi(basePath: String, httpClient: GitLabHttpClient) : Api(basePath, httpClient) {

    suspend fun get(): User = doGet()

}
