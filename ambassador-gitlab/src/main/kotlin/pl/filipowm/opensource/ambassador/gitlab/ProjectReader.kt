package pl.filipowm.opensource.ambassador.gitlab

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.AccessLevel
import org.gitlab4j.api.models.IssuesStatisticsFilter
import org.gitlab4j.api.models.Project
import org.slf4j.LoggerFactory
import pl.filipowm.opensource.ambassador.document.TextDetails
import pl.filipowm.opensource.ambassador.model.Commits
import pl.filipowm.opensource.ambassador.model.Issues
import pl.filipowm.opensource.ambassador.model.Members
import pl.filipowm.opensource.ambassador.model.stats.Timeline
import java.nio.file.Paths
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class ProjectReader(
    private val project: Project,
    private val gitLabApi: GitLabApi,
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
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
        return scope.async(dispatcher, block = block)
    }

    fun readIssues(): Deferred<Issues> {
        return async {
            val filter = IssuesStatisticsFilter().withIn(project.nameWithNamespace)
            val actual = projectIssuesStatisticsApi.getProjectIssuesStatistics(project.id, filter).counts
            val nowMinus90Days = LocalDate.now().minusDays(90)
            val nowMinus90DaysDate = Date.from(nowMinus90Days.atStartOfDay(ZoneId.systemDefault()).toInstant())
            val filterAfter90Days = filter.withUpdatedAfter(nowMinus90DaysDate)
            val last90Days = projectIssuesStatisticsApi.getProjectIssuesStatistics(project.id, filterAfter90Days).counts
            Issues(actual.all, actual.opened, actual.closed, last90Days.closed, last90Days.opened)
        }
    }

    fun readCommits(): Deferred<Commits> {
        return async { Commits(Timeline()) }
    }

    fun readMembers(): Deferred<Members> {
        val total = MutableInt()
        val membersMap = AccessLevel.values()
            .map { it.name to MutableInt() }
            .toMap()
        gitLabApi.projectApi
            .getAllMembersStream(project.id)
            .forEach {
                total.increment()
                membersMap.getOrDefault(it.accessLevel.name, MutableInt()).increment()
            }
        val finalMembers = membersMap.map { it.key to it.value.get() }
            .filter { it.second > 0 }
            .toMap()
        return async { Members(finalMembers, total.get()) }
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
            var content = Optional.ofNullable(path)
                .map { Paths.get(it) }
                .map { it.fileName.toString() }
                .flatMap { gitLabApi.repositoryFileApi.getOptionalFile(project.id, it, project.defaultBranch) }
                .map { TextDetails(it.contentSha256, it.size, toFullPath(it.filePath), unbaseContent(it.content)) }

            var transformed = transform(content)
            log.info("Read file '{}' in project {}", path, project.id)
            transformed
        }
    }

}