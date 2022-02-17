package com.roche.ambassador.gitlab

import com.roche.ambassador.OAuth2ClientProperties
import com.roche.ambassador.UserDetailsProvider
import com.roche.ambassador.exceptions.Exceptions
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.health.UnhealthyComponentException
import com.roche.ambassador.health.UnhealthyComponentException.Status
import com.roche.ambassador.model.files.RawFile
import com.roche.ambassador.model.group.Group
import com.roche.ambassador.model.group.GroupFilter
import com.roche.ambassador.model.project.*
import com.roche.ambassador.model.project.ci.CiExecution
import com.roche.ambassador.model.source.GroupSource
import com.roche.ambassador.model.source.IssuesManager
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.model.stats.Timeline
import com.roche.gitlab.api.GitLab
import com.roche.gitlab.api.exceptions.Exceptions.ForbiddenException
import com.roche.gitlab.api.exceptions.Exceptions.GitLabApiException
import com.roche.gitlab.api.exceptions.Exceptions.RateLimitReachedException
import com.roche.gitlab.api.exceptions.Exceptions.UnauthorizedException
import com.roche.gitlab.api.groups.GroupProjectListQuery
import com.roche.gitlab.api.groups.GroupsListQuery
import com.roche.gitlab.api.model.AccessLevelName
import com.roche.gitlab.api.model.AccessLevelName.*
import com.roche.gitlab.api.model.IssueStatisticsQuery
import com.roche.gitlab.api.model.UserState
import com.roche.gitlab.api.project.ProjectListQuery
import com.roche.gitlab.api.project.ProjectQuery
import com.roche.gitlab.api.project.commits.CommitsQuery
import com.roche.gitlab.api.project.events.EventsListQuery
import com.roche.gitlab.api.project.events.TargetType
import com.roche.gitlab.api.project.mergerequests.MergeRequest
import com.roche.gitlab.api.project.mergerequests.MergeRequestsQuery
import com.roche.gitlab.api.project.pipelines.PipelinesQuery
import com.roche.gitlab.api.project.pipelines.SimplePipeline
import com.roche.gitlab.api.utils.Pager
import com.roche.gitlab.api.utils.Pagination
import com.roche.gitlab.api.utils.Sort
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.net.SocketException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.streams.toList

@ExperimentalCoroutinesApi
class GitLabSource(val name: String, private val gitlab: GitLab) : ProjectSource, GroupSource {

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
        const val LOOKBACK_DAYS: Long = 90
    }

    override suspend fun ping() {
        try {
            gitlab.me().get()
        } catch (ex: Throwable) {
            val status = when (ex) {
                is UnauthorizedException -> Status.UNAUTHORIZED
                is ForbiddenException -> Status.UNAUTHORIZED
                is RateLimitReachedException -> Status.RATE_LIMITED
                is GitLabApiException -> Status.UNAVAILABLE
                is SocketException -> Status.UNAVAILABLE
                else -> Status.UNKNOWN
            }
            throw UnhealthyComponentException(status, "GitLab @ ${gitlab.url()} is unhealthy", ex)
        }
    }

    override suspend fun getById(id: String): Optional<Project> {
        log.info("Reading project {}", id)
        val prj = gitlab.projects()
            .withId(id.toLong())
            .get(ProjectQuery(true, true, true))
            .orElseThrow { Exceptions.NotFoundException("Project with ID $id not found") }
        return Optional.ofNullable(GitLabMapper.fromGitLabProject(prj))
    }

    private suspend fun <T, U> ProducerScope<U>.publishFromPager(
        pager: Pager<T>,
        mapper: (T) -> U,
        filter: (T) -> Boolean = { true }
    ) {
        for (page in pager) {
            for (item in page) {
                if (filter.invoke(item)) { // client-side filtering faking server behavior for missing filters
                    val mapped = mapper.invoke(item)
                    send(mapped)
                }
            }
        }
    }

    override fun flow(filter: ProjectFilter): Flow<Project> {
        val visibility = VisibilityMapper.fromAmbassador(filter.visibility!!)
        return channelFlow {
            if (filter.groups.isNotEmpty()) {
                val query = GroupProjectListQuery(
                    withCustomAttributes = false,
                    archived = filter.archived,
                    visibility = visibility,
                    includeSubgroups = true
                )
                filter.groups.forEach {
                    val pager = gitlab.groups()
                        .withPath(it)
                        .projects(query, Pagination(itemsPerPage = 50))
                    publishFromPager(pager, GitLabMapper::fromGitLabProject)
                }
            } else {
                val query = ProjectListQuery(
                    withStatistics = true,
                    withCustomAttributes = false,
                    archived = filter.archived,
                    visibility = visibility,
                    lastActivityAfter = filter.lastActivityAfter
                )
                val pager = gitlab.projects().paging(query, Pagination(itemsPerPage = 50))
                publishFromPager(pager, GitLabMapper::fromGitLabProject)
            }
        }
    }

    override fun name(): String = name

    override fun getOAuth2ClientProperties(): OAuth2ClientProperties = oAuth2ClientProperties

    override suspend fun readIssues(projectId: String): Issues {
        log.info("Reading project {} issues", projectId)
        val projectApi = gitlab.projects().withId(projectId.toLong())
        val actual = projectApi.issueStatistics().get().counts
        val nowMinus90Days = LocalDateTime.now().minusDays(LOOKBACK_DAYS)
        val last90Days = projectApi.issueStatistics().get(IssueStatisticsQuery(updatedAfter = nowMinus90Days)).counts
        log.info("Finished reading project {} issues", projectId)
        return Issues(actual.all, actual.opened, actual.closed, last90Days.all, last90Days.closed, last90Days.opened)
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
            since = LocalDateTime.now().minusYears(1),
            withStats = false
        )
        gitlab.projects()
            .withId(projectId.toLong())
            .repository()
            .commits()
            .paging(query, Pagination(itemsPerPage = 100))
            .forEach { timeline.increment(it.createdAt) }

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
        gitlab.projects()
            .withId(projectId.toLong())
            .releases()
            .paging(fromPagination = Pagination(itemsPerPage = 100))
            .forEach {
                if (it.releasedAt != null) {
                    timeline.increment(it.releasedAt!!)
                }
            }
        log.info("Finished reading project {} releases timeline", projectId)
        return timeline
    }

    override suspend fun readProtectedBranches(projectId: String): List<ProtectedBranch> {
        log.info("Reading project {} protected branches setup", projectId)
        val data = gitlab.projects()
            .withId(projectId.toLong())
            .protectedBranches()
            .stream()
            .map { GitLabMapper.from(it) }
            .toList()
        log.info("Finished reading project {} protected branches", projectId)
        return data
    }

    override suspend fun readMembers(projectId: String): List<Member> {
        log.info("Reading project {} members", projectId)
        val members = mutableListOf<Member>()
        gitlab.projects()
            .withId(projectId.toLong())
            .members()
            .paging(fromPagination = Pagination(itemsPerPage = 100))
            .forEach {
                if (it.state == UserState.ACTIVE && !isBotUser(projectId, it.username)) {
                    members.add(Member(it.id, it.name, it.email, it.username, mapAccessLevel(it.accessLevel)))
                }
            }
        return members.toList()
    }

    private fun isBotUser(projectId: String, username: String): Boolean {
        return username.contains("project_${projectId}_bot")
    }

    override suspend fun readPullRequests(projectId: String): List<PullRequest> {
        log.info("Reading project {} pull requests", projectId)
        val query = MergeRequestsQuery(
            state = MergeRequest.State.MERGED.name.lowercase(),
            updatedAfter = LocalDateTime.now().minusDays(LOOKBACK_DAYS)
        )
        val mergeRequests = mutableListOf<MergeRequest>()
        gitlab.projects()
            .withId(projectId.toLong())
            .mergeRequests()
            .paging(query, fromPagination = Pagination(itemsPerPage = 100))
            .forEach { mergeRequests += it }
        log.info("Finished reading project {} pull requests", projectId)
        return mergeRequests
            .map { PullRequest(it.createdAt, it.mergedAt ?: it.closedAt, GitLabMapper.fromGitLabState(it.state)) }
    }

    override suspend fun readCiExecutions(projectId: String, ref: String): List<CiExecution> {
        log.info("Reading project {} pipelines", projectId)
        val query = PipelinesQuery(
            scope = SimplePipeline.Scope.FINISHED,
            ref = ref,
            includeWithYamlErrors = false,
            updatedAfter = LocalDateTime.now().minusDays(LOOKBACK_DAYS)
        )
        val pipelines = mutableListOf<SimplePipeline>()
        gitlab.projects()
            .withId(projectId.toLong())
            .pipelines()
            .paging(query, fromPagination = Pagination(itemsPerPage = 100))
            .forEach { pipelines += it }
        log.info("Finished reading project {} pipelines", projectId)
        return pipelines
            .map { CiExecution(it.createdAt, it.updatedAt, GitLabMapper.fromGitLabState(it.status)) }
    }

    override suspend fun readComments(projectId: String): Timeline {
        log.info("Reading project {} issue comments", projectId)
        val query = EventsListQuery(action = "commented", target = "note", after = LocalDate.now().minusDays(LOOKBACK_DAYS))
        val timeline = Timeline()
        gitlab.projects()
            .withId(projectId.toLong())
            .events()
            .paging(query, Pagination(itemsPerPage = 100))
            .forEach {
                val note = it.note
                if (note != null && note.noteableType == TargetType.ISSUE) {
                    timeline.increment(note.createdAt)
                }
            }
        log.info("Finished reading project {} issue comments timeline", projectId)
        return timeline
    }

    override fun issues(): IssuesManager = GitLabIssuesManager(gitlab)

    override fun flowGroups(filter: GroupFilter): Flow<Group> {
        return channelFlow {
            val query = GroupsListQuery(
                withCustomAttributes = false,
                statistics = true
            )
            val pager = gitlab.groups().paging(query, Pagination(itemsPerPage = 50))
            publishFromPager(pager, GitLabMapper::fromGitLabGroup) {
                if (filter.visibility != null) {
                    filter.visibility!!.isMoreStrictThan(VisibilityMapper.fromGitLab(it.visibility))
                } else {
                    true
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun mapAccessLevel(gitlabAccessLevelName: AccessLevelName?): AccessLevel {
        return when (gitlabAccessLevelName) {
            ADMIN, MAINTAINER, OWNER -> AccessLevel.ADMIN
            DEVELOPER -> AccessLevel.WRITE
            REPORTER, GUEST -> AccessLevel.READ
            else -> AccessLevel.NONE
        }
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
