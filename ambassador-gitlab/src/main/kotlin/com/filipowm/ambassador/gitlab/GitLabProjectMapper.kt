package com.filipowm.ambassador.gitlab

import com.filipowm.ambassador.document.Language
import com.filipowm.ambassador.document.TextAnalyzingService
import com.filipowm.ambassador.document.TextDetails
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.model.files.Documentation
import com.filipowm.ambassador.model.files.File
import com.filipowm.ambassador.model.files.License
import com.filipowm.ambassador.model.project.Files
import com.filipowm.ambassador.model.project.Project
import com.filipowm.ambassador.model.stats.Statistics
import com.filipowm.gitlab.api.GitLab
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import org.slf4j.MDC
import java.util.*
import kotlin.coroutines.coroutineContext
import com.filipowm.gitlab.api.project.model.Project as GitLabProject

class GitLabProjectMapper(
    private val gitlab: GitLab,
    private val textAnalyzingService: TextAnalyzingService
) {

    companion object {
        private val log by LoggerDelegate()
    }

    @kotlinx.coroutines.ObsoleteCoroutinesApi
    suspend fun mapGitLabProjectToOpenSourceProject(gitlabProject: GitLabProject): Project {
        log.debug("Mapping project {} to OS project", gitlabProject.name)
        val visibility = VisibilityMapper.fromGitLab(gitlabProject.visibility!!)
        try {
            return withContext(coroutineContext) {
                supervisorScope {
                    MDC.put("project-id", gitlabProject.id.toString())
                    val reader = ProjectReader(gitlabProject, gitlab, this)
//                    val languages = reader.readLanguages()
//                    val readme = if (gitlabProject.readmeUrl != null) {
//                        reader.withFile(gitlabProject.readmeUrl!!) { analyzeDocument(it) }
//                    } else {
//                        async { Documentation.notExistent() }
//                    }
//                    val contributing = reader.withFile(Files.CONTRIBUTING_DEFAULT) { analyzeDocument(it) }
//                    val ci = reader.withFile(".gitlab-ci.yml") { contentToFile(it) }
//                    val changelog = reader.withFile(Files.CHANGELOG_DEFAULT) { contentToFile(it) }
//                    val gitignore = reader.withFile(Files.GITIGNORE_DEFAULT) { contentToFile(it) }
//                    val issues = reader.readIssues()
//                    val commits = reader.readCommits()
//                    val releases = reader.readReleases()
//                    val protectedBranches = reader.readProtectedBranches()
//                    val license = Optional.ofNullable(gitlabProject.license)
//                        .map { License(it.name, it.key, Language.ENGLISH.name, true, null, null, it.htmlUrl) }
//                        .orElse(License.notExistent())
//                    val files = Files(
//                        readme = readme.await(),
//                        contributingGuide = contributing.await(),
//                        ciDefinition = ci.await(),
//                        changelog = changelog.await(),
//                        license = license,
//                        gitignore = gitignore.await()
//                    )
                    val stats = if (gitlabProject.statistics != null) {
                        val glStats = gitlabProject.statistics!!
                        Statistics(
                            gitlabProject.forksCount!!,
                            gitlabProject.starCount!!,
                            glStats.commitCount,
                            glStats.jobArtifactsSize,
                            glStats.lfsObjectsSize,
                            glStats.packagesSize,
                            glStats.repositorySize,
                            glStats.storageSize,
                            glStats.wikiSize
                        )
                    } else {
                        Statistics(0, 0, 0, 0, 0, 0, 0, 0, 0)
                    }
//                    val features = Features(
//                        pullRequests = gitlabProject.mergeRequestsEnabled,
//                        issues = gitlabProject.issuesEnabled,
//                        cicd = gitlabProject.jobsEnabled,
//                        lfs = gitlabProject.lfsEnabled,
//                        containerRegistry = gitlabProject.containerRegistryEnabled,
//                        packages = gitlabProject.packagesEnabled,
//                        snippets = gitlabProject.snippetsEnabled,
//                        wiki = gitlabProject.wikiEnabled
//                    )

                    Project(
                        id = gitlabProject.id!!.toLong(),
                        url = gitlabProject.webUrl,
                        avatarUrl = gitlabProject.avatarUrl,
                        name = gitlabProject.name,
                        description = gitlabProject.description,
                        visibility = visibility,
                        stats = stats,
                        tags = Optional.ofNullable(gitlabProject.tagList).orElseGet { listOf() },
                        createdDate = gitlabProject.createdAt!!.toLocalDate(),
                        lastUpdatedDate = gitlabProject.lastActivityAt!!.toLocalDate(),
//                        commits = commits.await(),
//                        issues = issues.await(),
//                        files = files,
//                        languages = languages.await(),
//                        protectedBranches = protectedBranches.await(),
//                        releases = releases.await(),
                        defaultBranch = gitlabProject.defaultBranch
                    )
                }
            }
        } finally {
            MDC.clear()
        }
    }

    @kotlinx.coroutines.ObsoleteCoroutinesApi
    private suspend fun analyzeDocument(content: Optional<TextDetails>): Documentation {
        return if (content.isPresent) {
            textAnalyzingService.analyze(content.get())
        } else {
            Documentation.notExistent()
        }
    }

    private fun contentToFile(content: Optional<TextDetails>): File {
        return content
            .map { File(true, it.hash, null, it.size, it.path) }
            .orElseGet { File.notExistent() }
    }
}
