package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.Api
import com.filipowm.gitlab.api.IssueStatisticsApi
import com.filipowm.gitlab.api.client.GitLabHttpClient
import com.filipowm.gitlab.api.project.model.Project
import java.util.*

class ProjectApi internal constructor(basePath: String, client: GitLabHttpClient) : Api(basePath, client) {

    suspend fun get(
        withLicense: Boolean = false,
        withStatistics: Boolean = false,
        withCustomAttributes: Boolean = false
    ): Optional<Project> = get(ProjectQuery(withStatistics, withLicense, withCustomAttributes))

    suspend fun get(query: ProjectQuery): Optional<Project> = doGetOptional(query)

    suspend fun delete(): Unit = doDelete<Unit>()

    suspend fun languages(): Map<String, Float> = doGet(path = "languages")

    fun repository(): RepositoryApi = RepositoryApi("$basePath/repository", client)

    fun protectedBranches(): ProtectedBranchesApi = ProtectedBranchesApi("$basePath/protected_branches", client)

    fun releases(): ReleasesApi = ReleasesApi("$basePath/releases", client)

    fun issueStatistics(): IssueStatisticsApi = IssueStatisticsApi("$basePath/issues_statistics", client)

    fun members(): MembersApi = MembersApi("$basePath/members/all", client)

    fun mergeRequests(): MergeRequestsApi = MergeRequestsApi("$basePath/merge_requests", client)

    fun packages(): PackagesApi = PackagesApi("$basePath/packages", client)
}
