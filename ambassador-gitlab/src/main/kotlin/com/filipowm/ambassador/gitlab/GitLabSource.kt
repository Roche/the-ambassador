package com.filipowm.ambassador.gitlab

import com.filipowm.ambassador.OAuth2ClientProperties
import com.filipowm.ambassador.UserDetailsProvider
import com.filipowm.ambassador.exceptions.Exceptions
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.model.files.RawFile
import com.filipowm.ambassador.model.project.*
import com.filipowm.ambassador.model.source.ForkedProjectCriteria
import com.filipowm.ambassador.model.source.InvalidProjectCriteria
import com.filipowm.ambassador.model.source.PersonalProjectCriteria
import com.filipowm.ambassador.model.source.ProjectSource
import com.filipowm.ambassador.model.stats.Timeline
import com.filipowm.gitlab.api.GitLab
import com.filipowm.gitlab.api.model.AccessLevel
import com.filipowm.gitlab.api.model.AccessLevelName
import com.filipowm.gitlab.api.model.IssueStatisticsQuery
import com.filipowm.gitlab.api.project.CommitsQuery
import com.filipowm.gitlab.api.project.ProjectListQuery
import com.filipowm.gitlab.api.project.ProjectQuery
import com.filipowm.gitlab.api.utils.Pagination
import com.filipowm.gitlab.api.utils.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.time.LocalDateTime
import java.util.*
import kotlin.streams.toList
import com.filipowm.gitlab.api.project.model.Project as GitLabProject

class GitLabSource(
    private val gitlab: GitLab
) : ProjectSource<GitLabProject> {

    private val oAuth2ClientProperties = OAuth2ClientProperties(
        name = name(),
        authorizationUri = "${gitlab.url()}/oauth/authorize",
        jwkSetUri = "${gitlab.url()}/oauth/discovery/keys",
        tokenUri = "${gitlab.url()}/oauth/token",
        userInfoUri = "${gitlab.url()}/api/v4/user",
        usernameAttributeName = "username",
        scopes = setOf("read_user")
    )

    companion object {
        private val log by LoggerDelegate()
    }

    override suspend fun getById(id: String): Optional<Project> {
        log.info("Reading project {}", id)
        val prj = gitlab.projects()
            .withId(id.toLong())
            .get(ProjectQuery(true, true, true))
            .orElseThrow { Exceptions.NotFoundException("Project with ID $id not found") }
        return Optional.ofNullable(GitLabProjectMapper.mapGitLabProjectToOpenSourceProject(prj))
    }

    override suspend fun flow(filter: ProjectFilter): Flow<GitLabProject> {
        val visibility = VisibilityMapper.fromAmbassador(filter.visibility!!)
        val query = ProjectListQuery(
            withStatistics = true,
            withCustomAttributes = false,
            archived = filter.archived,
            visibility = visibility,
            lastActivityAfter = filter.lastActivityAfter
        )
        return channelFlow {
            val pager = gitlab.projects().paging(query, Pagination(itemsPerPage = 50))
            for (page in pager) {
                for (project in page) {
                    log.debug("Publishing project {}", project.id)
                    send(project)
                }
            }
        }
    }

    override fun name(): String = "GitLab"

    override suspend fun map(input: GitLabProject): Project = GitLabProjectMapper.mapGitLabProjectToOpenSourceProject(input)

    override fun getInvalidProjectCriteria(): InvalidProjectCriteria<GitLabProject> = GitLabInvalidProjectCriteria
    override fun resolveName(project: GitLabProject): String = project.nameWithNamespace!!
    override fun resolveId(project: GitLabProject): String = project.id!!.toString()
    override fun getForkedProjectCriteria(): ForkedProjectCriteria<GitLabProject> = GitLabForkedProjectCriteria
    override fun getPersonalProjectCriteria(): PersonalProjectCriteria<GitLabProject> = GitLabPersonalProjectCriteria
    override fun getOAuth2ClientProperties(): OAuth2ClientProperties = oAuth2ClientProperties

    override suspend fun readIssues(projectId: String): Issues {
        log.info("Reading project {} issues", projectId)
        val projectApi = gitlab.projects().withId(projectId.toLong())
        val actual = projectApi.issueStatistics().get().counts
        val nowMinus90Days = LocalDateTime.now().minusDays(90)
        val last90Days = projectApi.issueStatistics().get(IssueStatisticsQuery(updatedAfter = nowMinus90Days)).counts
        log.info("Finished reading project {} issues", projectId)
        return Issues(actual.all, actual.opened, actual.closed, last90Days.closed, last90Days.opened)
    }

    override suspend fun readContributors(projectId: String): List<Contributor> {
        return gitlab.projects().withId(projectId.toLong()).repository().getContributors(Sort.desc("commits"))
            .map { Contributor(it.name, it.email, it.commits, null) }
    }

    override suspend fun readLanguages(projectId: String): Map<String, Float> {
        log.info("Reading project {} languages", projectId)
        val languages = gitlab.projects().withId(projectId.toLong()).languages()
        log.info("Read project {} languages", projectId)
        return languages
    }

    override suspend fun readCommits(projectId: String, ref: String): Timeline {
        val timeline = Timeline()
        log.info("Reading project {} commits timeline", projectId)
        val query = CommitsQuery(
            refName = ref,
            since = LocalDateTime.now().minusDays(90),
            until = LocalDateTime.now(),
            withStats = false
        )
        val paging = gitlab.projects().withId(projectId.toLong())
            .repository().commits().paging(query, Pagination(itemsPerPage = 50))

        for (commitsPage in paging) {
            for (commit in commitsPage) {
                timeline.increment(commit.createdAt)
            }
        }
        log.info("Finished reading project {} commits timeline", projectId)
        return timeline
    }

    override suspend fun readFile(projectId: String, path: String, ref: String): Optional<RawFile> {
        return gitlab.projects().withId(projectId.toLong())
            .repository()
            .files()
            .get(path, ref)
            .map { RawFile(true, it.contentSha256, null, it.size, it.filePath, it.getRawContent().orElse(null)) }
    }

    override suspend fun readReleases(projectId: String): Timeline {
        log.info("Reading project {} releases timeline", projectId)
        val timeline = Timeline()
        for (releasePage in gitlab.projects().withId(projectId.toLong()).releases().paging()) {
            for (release in releasePage) {
                if (release.releasedAt != null) {
                    timeline.increment(release.releasedAt!!)
                }
            }
        }
        log.info("Finished reading project {} releases timeline", projectId)
        return timeline
    }

    override suspend fun readProtectedBranches(projectId: String): List<ProtectedBranch> {
        log.info("Reading project {} protected branches setup", projectId)
        val data = gitlab.projects().withId(projectId.toLong()).protectedBranches().stream()
            .map { ProtectedBranch(it.name, checkHasNoAccess(it.mergeAccessLevels), checkHasNoAccess(it.pushAccessLevels)) }
            .toList()
        log.info("Finished reading project {} protected branches", projectId)
        return data
    }

    private fun checkHasNoAccess(accessLevels: List<AccessLevel>): Boolean {
        return accessLevels.none { it.accessLevel == AccessLevelName.NONE }
    }

    override fun userDetailsProvider(attributes: Map<String, Any>): UserDetailsProvider = object : UserDetailsProvider {
        override fun getName(): String = attributes["name"] as String

        override fun getUsername(): String = attributes["username"] as String

        override fun getEmail(): String = attributes["email"] as String

        override fun getAvatarUrl(): String = attributes["avatar_url"] as String

        override fun getWebUrl(): String = attributes["web_url"] as String

        override fun isAdmin(): Boolean = attributes["is_admin"] as Boolean

    }
}
