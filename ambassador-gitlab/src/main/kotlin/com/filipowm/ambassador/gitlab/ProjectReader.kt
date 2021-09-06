package com.filipowm.ambassador.gitlab

import com.filipowm.ambassador.document.TextDetails
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.model.Contributor
import com.filipowm.ambassador.model.Contributors
import com.filipowm.ambassador.model.Issues
import com.filipowm.ambassador.model.ProtectedBranch
import com.filipowm.ambassador.model.stats.Timeline
import com.filipowm.gitlab.api.GitLab
import com.filipowm.gitlab.api.model.AccessLevel
import com.filipowm.gitlab.api.model.AccessLevelName
import com.filipowm.gitlab.api.model.IssueStatisticsQuery
import com.filipowm.gitlab.api.project.CommitsQuery
import com.filipowm.gitlab.api.project.model.Project
import com.filipowm.gitlab.api.utils.Pagination
import com.filipowm.gitlab.api.utils.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*
import kotlin.streams.toList

class ProjectReader(
    private val project: Project,
    gitlab: GitLab,
    private val scope: CoroutineScope,
) {

    companion object {
        private val log by LoggerDelegate()
    }

    private val projectApi = gitlab.projects().withId(project.id!!.toLong())

    suspend fun readIssues(): Deferred<Issues> {
        return async {
            log.info("Reading project {} issues", project.id)
            val actual = projectApi.issueStatistics().get().counts
            val nowMinus90Days = LocalDateTime.now().minusDays(90)
            val last90Days = projectApi.issueStatistics().get(IssueStatisticsQuery(updatedAfter = nowMinus90Days)).counts
            log.info("Finished reading project {} issues", project.id)
            Issues(actual.all, actual.opened, actual.closed, last90Days.closed, last90Days.opened)
        }
    }

    suspend fun readContributors(): Deferred<Contributors> {
        return async {
            log.info("Reading project {} contributors", project.id)
            val all = projectApi
                .repository()
                .getContributors(Sort.desc("commits"))
                .map { Contributor(it.name, it.email, it.commits, null) }

            val totalContributors = all.size
            val top3 = all.take(3)
            log.info("Finished reading project {} contributors", project.id)
            Contributors(totalContributors, top3)
        }
    }

    suspend fun readProtectedBranches(): Deferred<List<ProtectedBranch>> {
        return async {
            log.info("Reading project {} protected branches setup", project.id)
            val data = projectApi.protectedBranches().stream()
                .map { ProtectedBranch(it.name, checkHasNoAccess(it.mergeAccessLevels), checkHasNoAccess(it.pushAccessLevels)) }
                .toList()
            log.info("Finished reading project {} protected branches", project.id)
            data
        }
    }

    private fun checkHasNoAccess(accessLevels: List<AccessLevel>): Boolean {
        return accessLevels.none { it.accessLevel == AccessLevelName.NONE }
    }

    suspend fun readReleases(): Deferred<Timeline> {
        return async {
            log.info("Reading project {} releases timeline", project.id)
            val timeline = Timeline()
            projectApi.releases().stream(Sort.desc("released_at"))
                .filter { it.releasedAt != null }
                .forEach { timeline.add(it.releasedAt!!, 1) }

            log.info("Finished reading project {} releases timeline", project.id)
            timeline
        }

    }

    suspend fun readCommits(): Deferred<Timeline> {
        val timeline = Timeline()
        if (project.defaultBranch != null) {
            return async {
                log.info("Reading project {} commits timeline", project.id)
                val query = CommitsQuery(
                    refName = project.defaultBranch,
                    since = LocalDateTime.now().minusDays(90),
                    until = LocalDateTime.now(),
                    withStats = false
                )
                projectApi.repository()
                    .commits()
                    .stream(query, Pagination(itemsPerPage = 50))
                    .forEach { timeline.add(it.createdAt, 1) }
                log.info("Finished reading project {} commits timeline", project.id)
                timeline
            }
        } else {
            log.warn("No default branch in repo in project {}. Skipping reading commits.", project.id)
            return async { timeline }
        }
    }

    suspend fun readLanguages(): Deferred<Map<String, Float>> {
        return async {
            log.info("Reading project {} languages", project.id)
            val languages = projectApi.languages()
            log.info("Read project {} languages", project.id)
            languages
        }
    }

    private fun toFullPath(path: String): String {
        return "${project.webUrl}/-/blob/${project.defaultBranch}/$path"
    }

    suspend fun <T> withFile(path: String, transform: (Optional<TextDetails>) -> T): Deferred<T> {
        return async {
            log.info("Reading file '{}' in project {}", path, project.id)
            var content = Optional.empty<TextDetails>()
            if (project.defaultBranch.isNullOrBlank()) {
                log.error("Project {} (id={}) has no branch", project.name, project.id)
            } else {
                val branch = Optional.ofNullable(project.defaultBranch).orElse("master")
                val fullPath = Paths.get(path).fileName.toString()
                content = readFile(fullPath, branch)
                log.info("Read file '{}' in project {}", path, project.id)
            }
            transform(content)
        }
    }

    private suspend fun readFile(path: String, branch: String): Optional<TextDetails> {
        return projectApi.repository()
            .files().get(path, branch)
            .map { TextDetails(it.contentSha256, it.size, toFullPath(it.filePath), it.getRawContent().orElse(null)) }
    }

    private fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return scope.async(scope.coroutineContext + SupervisorJob(), block = block)
    }
}
