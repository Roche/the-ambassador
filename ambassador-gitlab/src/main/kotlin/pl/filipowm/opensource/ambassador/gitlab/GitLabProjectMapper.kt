package pl.filipowm.opensource.ambassador.gitlab

import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.gitlab4j.api.GitLabApi
import org.slf4j.MDC
import pl.filipowm.opensource.ambassador.document.Language
import pl.filipowm.opensource.ambassador.document.TextAnalyzingService
import pl.filipowm.opensource.ambassador.document.TextDetails
import pl.filipowm.opensource.ambassador.extensions.LoggerDelegate
import pl.filipowm.opensource.ambassador.model.Features
import pl.filipowm.opensource.ambassador.model.Files
import pl.filipowm.opensource.ambassador.model.Project
import pl.filipowm.opensource.ambassador.model.files.Documentation
import pl.filipowm.opensource.ambassador.model.files.File
import pl.filipowm.opensource.ambassador.model.files.License
import pl.filipowm.opensource.ambassador.model.stats.Statistics
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.coroutines.coroutineContext
import org.gitlab4j.api.models.Project as GitLabProject

class GitLabProjectMapper(
    private val gitlabApi: GitLabApi,
    private val textAnalyzingService: TextAnalyzingService
) {

    companion object {
        private val log by LoggerDelegate()
    }

    suspend fun mapGitLabProjectToOpenSourceProject(gitlabProject: GitLabProject): Project {
        log.info("Mapping project {} to OS project", gitlabProject.name)
        val visibility = VisibilityMapper.fromGitLab(gitlabProject.visibility)
        try {
            return withContext(coroutineContext) {
                MDC.put("project-id", gitlabProject.id.toString())
                val reader = ProjectReader(gitlabProject, gitlabApi, this)
                val languages = reader.readLanguages()
                val readme = if (gitlabProject.readmeUrl != null)
                    reader.withFile(gitlabProject.readmeUrl) { analyzeDocument(it) }
                else
                    async { Documentation.notExistent() }
                val contributing = reader.withFile(Files.CONTRIBUTING_DEFAULT) { analyzeDocument(it) }
                val ci = reader.withFile(".gitlab-ci.yml") { contentToFile(it) }
                val changelog = reader.withFile(Files.CHANGELOG_DEFAULT) { contentToFile(it) }
                val gitignore = reader.withFile(Files.GITIGNORE_DEFAULT) { contentToFile(it) }
                val issues = reader.readIssues()
                val commits = reader.readCommits()
                val contributors = reader.readContributors()
                val releases = reader.readReleases()
                val protectedBranches = reader.readProtectedBranches()
                val license = Optional.ofNullable(gitlabProject.license)
                    .map { License(it.name, it.key, Language.ENGLISH.name, true, null, null, it.htmlUrl) }
                    .orElse(License.notExistent())
                val files = Files(
                    readme = readme.await(),
                    contributingGuide = contributing.await(),
                    ciDefinition = ci.await(),
                    changelog = changelog.await(),
                    license = license,
                    gitignore = gitignore.await()
                )
                val stats = Statistics(
                    gitlabProject.forksCount,
                    gitlabProject.starCount,
                    gitlabProject.statistics.commitCount,
                    gitlabProject.statistics.jobArtifactsSize,
                    gitlabProject.statistics.lfsObjectsSize,
                    gitlabProject.statistics.packagesSize,
                    gitlabProject.statistics.repositorySize,
                    gitlabProject.statistics.storageSize,
                    gitlabProject.statistics.wikiSize
                )
                val features = Features(
                    pullRequests = gitlabProject.mergeRequestsEnabled,
                    issues = gitlabProject.issuesEnabled,
                    cicd = gitlabProject.jobsEnabled,
                    lfs = gitlabProject.lfsEnabled,
                    containerRegistry = gitlabProject.containerRegistryEnabled,
                    packages = gitlabProject.packagesEnabled ?: false,
                    snippets = gitlabProject.snippetsEnabled ?: false,
                    wiki = gitlabProject.wikiEnabled ?: false
                )
                Project(
                    id = gitlabProject.id.toLong(),
                    url = gitlabProject.webUrl,
                    avatarUrl = gitlabProject.avatarUrl,
                    name = gitlabProject.name,
                    description = gitlabProject.description,
                    visibility = visibility,
                    stats = stats,
                    tags = gitlabProject.tagList,
                    createdDate = toLocalDate(gitlabProject.createdAt),
                    lastUpdatedDate = toLocalDate(gitlabProject.lastActivityAt),
                    commits = commits.await(),
                    issues = issues.await(),
                    files = files,
                    languages = languages.await(),
                    contributors = contributors.await(),
                    protectedBranches = protectedBranches.await(),
                    releases = releases.await(),
                    features = features,
                    defaultBranch = gitlabProject.defaultBranch
                )
            }
        } finally {
            MDC.clear()
        }
    }

    private fun analyzeDocument(content: Optional<TextDetails>): Documentation {
        return content
            .map { textAnalyzingService.analyze(it) }
            .orElseGet { Documentation.notExistent() }
    }

    private fun contentToFile(content: Optional<TextDetails>): File {
        return content
            .map { File(true, it.hash, null, it.size, it.path) }
            .orElseGet { File.notExistent() }
    }

}

private fun toLocalDate(date: Date): LocalDate {
    return Instant.ofEpochMilli(date.time)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}