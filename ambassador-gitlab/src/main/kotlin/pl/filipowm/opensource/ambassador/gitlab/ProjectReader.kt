package pl.filipowm.opensource.ambassador.gitlab

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.AccessLevel
import org.gitlab4j.api.models.BranchAccessLevel
import org.gitlab4j.api.models.IssuesStatisticsFilter
import org.gitlab4j.api.models.Project
import org.slf4j.LoggerFactory
import pl.filipowm.opensource.ambassador.document.TextDetails
import pl.filipowm.opensource.ambassador.gitlab.api.ProjectIssuesStatisticsApi
import pl.filipowm.opensource.ambassador.model.Contributor
import pl.filipowm.opensource.ambassador.model.Contributors
import pl.filipowm.opensource.ambassador.model.Issues
import pl.filipowm.opensource.ambassador.model.ProtectedBranch
import pl.filipowm.opensource.ambassador.model.stats.Timeline
import java.nio.file.Paths
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.streams.toList

class ProjectReader(
    private val project: Project,
    private val gitLabApi: GitLabApi,
    private val scope: CoroutineScope,
    private val projectIssuesStatisticsApi: ProjectIssuesStatisticsApi = ProjectIssuesStatisticsApi(gitLabApi)
) {

    internal class MutableInt {
        private var value = 0
        fun increment() {
            ++value
        }

        fun get(): Int {
            return value
        }
    }

    private val log = LoggerFactory.getLogger(ProjectReader::class.java)

    private fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> {

        return scope.async(block = block)
    }

    fun readIssues(): Deferred<Issues> {
        return async {
            log.info("Reading project {} issues", project.id)
            val filter = IssuesStatisticsFilter().withIn(project.nameWithNamespace)
            val actual = projectIssuesStatisticsApi.getProjectIssuesStatistics(project.id, filter).counts
            val nowMinus90Days = LocalDate.now().minusDays(90)
            val nowMinus90DaysDate = Date.from(nowMinus90Days.atStartOfDay(ZoneId.systemDefault()).toInstant())
            val filterAfter90Days = filter.withUpdatedAfter(nowMinus90DaysDate)
            val last90Days = projectIssuesStatisticsApi.getProjectIssuesStatistics(project.id, filterAfter90Days).counts
            log.info("Finished reading project {} issues", project.id)
            Issues(actual.all, actual.opened, actual.closed, last90Days.closed, last90Days.opened)
        }
    }

    private fun get90DaysAgo(): Date {
        val nowMinus90Days = LocalDate.now().minusDays(90)
        return Date.from(nowMinus90Days.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun readContributors(): Deferred<Contributors> {
        return async {
            log.info("Reading project {} contributors", project.id)
            val all =
                gitLabApi.repositoryApi
                    .getContributorsStream(project.id)
                    .map { Contributor(it.name, it.email, it.commits, it.avatarUrl) }
                    .toList()

            val data = all
            val top3 = data.stream()
                .sorted { contributor, contributor2 -> contributor2.commits.compareTo(contributor.commits) }
                .limit(3)
                .toList()
            log.info("Finished reading project {} contributors", project.id)
            Contributors(data.size, top3)
        }
    }

    fun readProtectedBranches(): Deferred<List<ProtectedBranch>> {
        return async {
            log.info("Reading project {} protected branches setup", project.id)
            val data = gitLabApi.protectedBranchesApi
                .getProtectedBranchesStream(project.id)
                .map { ProtectedBranch(it.name, checkHasNoAccess(it.mergeAccessLevels), checkHasNoAccess(it.pushAccessLevels)) }
                .toList()

            log.info("Finished reading project {} protected branches", project.id)
            data
        }
    }

    private fun checkHasNoAccess(accessLevels: List<BranchAccessLevel>): Boolean {
        return accessLevels.none { it.accessLevel == AccessLevel.NONE }
    }

    fun readReleases(): Deferred<Timeline> {
        return async {
            log.info("Reading project {} releases timeline", project.id)
            val timeline = Timeline()
            gitLabApi.releasesApi
                .getReleasesStream(project.id)
                .filter { it.releasedAt != null }
                .forEach { timeline.add(it.releasedAt, 1) }

            log.info("Finished reading project {} releases timeline", project.id)
            timeline
        }
    }

    fun readCommits(): Deferred<Timeline> {
        return async {
            val timeline = Timeline()
            if (project.defaultBranch != null) {
                log.info("Reading project {} commits timeline", project.id)
                gitLabApi.commitsApi.getCommitsStream(
                    project.id,
                    project.defaultBranch,
                    get90DaysAgo(), Date()
                )
                    .forEach { timeline.add(it.createdAt, 1) }
            } else {
                log.warn("No default branch in repo in project {}. Skipping reading commits.", project.id)
            }
            log.info("Finished reading project {} commits timeline", project.id)
            timeline.by().weeks()
        }
    }

    fun readFetches(): Deferred<Int> {
        return async {
            gitLabApi.projectApi.getOptionalProjectStatistics(project.id)
                .map { it.fetches }
                .map { it.total }
                .orElse(0)
        }
    }

    fun readLanguages(): Deferred<Map<String, Float>> {
        return async {
            log.info("Reading project {} languages", project.id)
            val languages = gitLabApi.projectApi.getProjectLanguages(project.id)
            log.info("Read project {} languages", project.id)
            languages
        }
    }

    private fun toFullPath(path: String): String {
        return "${project.webUrl}/-/blob/${project.defaultBranch}/${path}"
    }

    private fun unbaseContent(content: String): String {
        return String(Base64.getDecoder().decode(content))
    }

    fun <T> withFile(path: String, transform: (Optional<TextDetails>) -> T): Deferred<T> {
        return async {
            log.info("Reading file '{}' in project {}", path, project.id)
            var content = Optional.empty<TextDetails>()
            if (project.defaultBranch.isNullOrBlank()) {
                log.error("Project {} (id={}) has no branch", project.name, project.id)
            } else {
                content = Optional.ofNullable(path)
                    .map { Paths.get(it) }
                    .map { it.fileName.toString() }
                    .flatMap { gitLabApi.repositoryFileApi.getOptionalFile(project.id, it, project.defaultBranch) }
                    .map { TextDetails(it.contentSha256, it.size, toFullPath(it.filePath), unbaseContent(it.content)) }

                log.info("Read file '{}' in project {}", path, project.id)
            }
            var transformed = transform(content)
            transformed
        }
    }

}