package com.roche.gitlab.api.project.issues

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.client.GitLabHttpClient
import java.util.*

class IssuesApi(basePath: String, httpClient: GitLabHttpClient) : Api(basePath, httpClient) {

    suspend fun create(request: CreateIssueRequest): Optional<Issue> {
        val issue = doPost<Issue>(body = request)
        return Optional.ofNullable(issue)
    }

    suspend fun update(request: UpdateIssueRequest): Optional<Issue> {
        val issue = doPut<Issue>(path = "${request.issueId}", body = request)
        return Optional.ofNullable(issue)
    }

    suspend fun get(id: Long): Optional<Issue> = doGetOptional(path = "$id")

}