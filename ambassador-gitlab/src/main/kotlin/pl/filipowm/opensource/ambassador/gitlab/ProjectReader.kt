package pl.filipowm.opensource.ambassador.gitlab

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.AccessLevel
import org.gitlab4j.api.models.Project
import org.slf4j.LoggerFactory
import pl.filipowm.opensource.ambassador.model.Commits
import pl.filipowm.opensource.ambassador.model.Issues
import pl.filipowm.opensource.ambassador.model.Members
import pl.filipowm.opensource.ambassador.model.PullRequests
import pl.filipowm.opensource.ambassador.model.stats.Timeline
import java.nio.file.Paths
import java.util.*

class ProjectReader(
    private val project: Project,
    private val gitLabApi: GitLabApi,
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher
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
        return async { Issues() }
    }

    fun readCommits(): Deferred<Commits> {
        return async { Commits(Timeline()) }
    }

    fun readPullRequests(): Deferred<PullRequests> {
        return async { PullRequests() }
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

    fun <T> withFile(path: String, transform: (Optional<String>) -> T): Deferred<T> {
        return async {
            log.info("Reading file '{}' in project {}", path, project.id)
            var content = Optional.ofNullable(path)
                .map { Paths.get(it) }
                .map { it.fileName.toString() }
                .flatMap { gitLabApi.repositoryFileApi.getOptionalFile(project.id, it, project.defaultBranch) }
                .map { it.content }
                .map { Base64.getDecoder().decode(it) }
                .map { String(it) }
            var transformed = transform(content)
            log.info("Read file '{}' in project {}", path, project.id)
            transformed
        }
    }

}