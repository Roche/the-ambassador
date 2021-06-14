package pl.filipowm.opensource.ambassador.gitlab

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import org.gitlab4j.api.GitLabApi
import org.slf4j.LoggerFactory
import pl.filipowm.opensource.ambassador.document.Hash
import pl.filipowm.opensource.ambassador.document.Language
import pl.filipowm.opensource.ambassador.document.TextAnalyzingService
import pl.filipowm.opensource.ambassador.model.*
import pl.filipowm.opensource.ambassador.model.files.Documentation
import pl.filipowm.opensource.ambassador.model.files.File
import pl.filipowm.opensource.ambassador.model.files.License
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import org.gitlab4j.api.models.Project as GitLabProject
import org.gitlab4j.api.models.ProjectFilter as GitLabProjectFilter

@ObsoleteCoroutinesApi
class GitLabProjectRepository(
    private val gitlabApi: GitLabApi,
    private val textAnalyzingService: TextAnalyzingService
) : ProjectRepository {

    private val log = LoggerFactory.getLogger(GitLabProjectRepository::class.java)

    override fun getById(id: String): Optional<Project> {
        return gitlabApi.projectApi
            .getOptionalProject(id, true, true, true)
            .map { mapGitLabProjectToOpenSourceProject(it) }
    }

    override fun getByPath(path: String): Optional<Project> {
        return getById(path)
    }

    override fun flow(filter: ProjectFilter?): Flow<Project> {
        val glFilter = GitLabProjectFilter()
            .withArchived(filter?.archived)
            .withStatistics(true)
            .withCustomAttributes(true)

        return flow {

            val pager = gitlabApi.projectApi.getProjects(glFilter, 50)
            while (pager.hasNext()) {
                for (project in pager.next()) {
                    emit(project)
                }
            }
        }.map { mapGitLabProjectToOpenSourceProject(it) }
    }

    private fun mapGitLabProjectToOpenSourceProject(gitlabProject: GitLabProject): Project {
        val visibility = when (gitlabProject.visibility) {
            org.gitlab4j.api.models.Visibility.INTERNAL -> Visibility.INTERNAL
            org.gitlab4j.api.models.Visibility.PRIVATE -> Visibility.PRIVATE
            org.gitlab4j.api.models.Visibility.PUBLIC -> Visibility.PUBLIC
            else -> Visibility.UNKNOWN
        }
        val dispatcher = newFixedThreadPoolContext(10, "project-${gitlabProject.id}")
        return runBlocking {
            val reader = ProjectReader(gitlabProject, gitlabApi, this, dispatcher)
            val langs = reader.readLanguages()
            val readme = if (gitlabProject.readmeUrl != null)
                reader.withFile(gitlabProject.readmeUrl) { analyzeDocument(it) }
            else
                async { Documentation.notExistent() }
            val contributing = reader.withFile("CONTRIBUTING.md") { analyzeDocument(it) }
            val ci = reader.withFile(".gitlab-ci.yml") { contentToFile(it) }
            val changelog = reader.withFile("CHANGELOG.md") { contentToFile(it) }
            val gitignore = reader.withFile(".gitignore") { contentToFile(it) }
            val issues = reader.readIssues()
            val commits = reader.readCommits()
            val members = reader.readMembers()
            val license = Optional.ofNullable(gitlabProject.license)
                .map { License(Language.ENGLISH.name, it.name, it.key, true, it.htmlUrl, null, it.sourceUrl) }
                .orElse(License.notExistent())
            val files = Files(
                readme = readme.await(),
                contributingGuide = contributing.await(),
                ciDefinition = ci.await(),
                changelog = changelog.await(),
                license = license,
                gitignore = gitignore.await()
            )
            Project(
                id = gitlabProject.id.toLong(),
                url = gitlabProject.webUrl,
                avatarUrl = gitlabProject.avatarUrl,
                name = gitlabProject.name,
                description = gitlabProject.description,
                visibility = visibility,
                forksCount = gitlabProject.forksCount,
                starsCount = gitlabProject.starCount,
                tags = gitlabProject.tagList,
                createdDate = toLocalDate(gitlabProject.createdAt),
                lastUpdatedDate = toLocalDate(gitlabProject.lastActivityAt),
                commits = commits.await(),
                issues = issues.await(),
                files = files,
                languages = langs.await(),
                members = members.await()
            )
        }
    }

    override fun save(project: Project) {
        TODO("Not yet implemented")
    }

    private fun analyzeDocument(content: Optional<String>): Documentation {
        return content
            .map { textAnalyzingService.analyze(it) }
            .orElseGet { Documentation.notExistent() }
    }

    private fun contentToFile(content: Optional<String>): File {
        return content
            .map { File(true, Hash.sha256OrNull(it), null, it.length, "xa") }
            .orElseGet { File.notExistent() }
    }
}

private fun toLocalDate(date: Date): LocalDate {
    return Instant.ofEpochMilli(date.time)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}