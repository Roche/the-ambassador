package com.roche.gitlab.api.project

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.IssueStatisticsApi
import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.project.branches.ProtectedBranchesApi
import com.roche.gitlab.api.project.events.EventsApi
import com.roche.gitlab.api.project.issues.IssuesApi
import com.roche.gitlab.api.project.members.MembersApi
import com.roche.gitlab.api.project.mergerequests.MergeRequestsApi
import com.roche.gitlab.api.project.model.Project
import com.roche.gitlab.api.project.packages.PackagesApi
import com.roche.gitlab.api.project.pipelines.PipelinesApi
import com.roche.gitlab.api.project.releases.ReleasesApi
import com.roche.gitlab.api.project.repository.RepositoryApi
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

    fun events(): EventsApi = EventsApi("$basePath/events", client)

    fun issues(): IssuesApi = IssuesApi("$basePath/issues", client)

    fun pipelines(): PipelinesApi = PipelinesApi("$basePath/pipelines", client)
}
