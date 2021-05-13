package pl.filipowm.innersource.ambassador.gitlab

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.gitlab4j.api.GitLabApi
import pl.filipowm.innersource.ambassador.model.*
import pl.filipowm.innersource.ambassador.model.files.Documentation
import pl.filipowm.innersource.ambassador.model.files.DocumentationComplexity
import pl.filipowm.innersource.ambassador.model.files.License
import pl.filipowm.innersource.ambassador.model.stats.Timeline
import java.nio.file.Paths
import java.time.Instant
import java.time.ZoneId
import java.util.*
import org.gitlab4j.api.models.Project as GitLabProject
import org.gitlab4j.api.models.ProjectFilter as GitLabProjectFilter

class GitLabProjectRepository(
    private val gitlabApi: GitLabApi
) : ProjectRepository {

    override fun getById(id: String): Optional<Project> {
        return gitlabApi.projectApi
            .getOptionalProject(id, true, true, true)
            .map { mapGitLabProjectToInnerSourceProject(it) }
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
        }.map { mapGitLabProjectToInnerSourceProject(it) }
    }

    override fun save(project: Project) {
        TODO("Not yet implemented")
    }

//    suspend override fun flow(filter: ProjectFilter?) : ReceiveChannel<Project> {

//        var glFilter = GitLabProjectFilter()
//            .withArchived(filter?.archived)
//            .withStatistics(true)
//            .withCustomAttributes(true)

//        return flow {
//
//            val pager = gitlabApi.projectApi.getProjects(glFilter, 50)
//            while (pager.hasNext()) {
//                for (project in pager.next()) {
//                    emit(project)
//                }
//            }
//        }
//            .flowOn(Dispatchers.Default)
//            .buffer()
//            .map { mapGitLabProjectToInnerSourceProject(it) }
}

private fun mapGitLabProjectToInnerSourceProject(gitlabProject: GitLabProject): Project {
    val visibility = when (gitlabProject.visibility) {
        org.gitlab4j.api.models.Visibility.INTERNAL -> Visibility.INTERNAL
        org.gitlab4j.api.models.Visibility.PRIVATE -> Visibility.PRIVATE
        org.gitlab4j.api.models.Visibility.PUBLIC -> Visibility.PUBLIC
        else -> Visibility.UNKNOWN
    }

    val readme = Optional.ofNullable(gitlabProject.readmeUrl)
        .map { Paths.get(it) }
        .map { it.fileName.toString() }
//            .flatMap { gitlabApi.repositoryFileApi.getOptionalFile(gitlabProject.id, it, gitlabProject.defaultBranch) }
//            .map { Documentation("ENGLISH", it.size, it.contentSha256, null, true) }
        .map { Documentation("ENGLISH", 2, "x", null, true) }
        .orElse(Documentation.notExistent())

    val license = Optional.ofNullable(gitlabProject.license)
        .map { License("ENGLISH", 0, null, DocumentationComplexity(0f), it.name, it.key, true) }
        .orElse(License.notExistent())

//       gitlabApi.eventsApi.getProjectEvents(5, Constants.ActionType.CREATED, Constants.TargetType.)
    return Project(
        gitlabProject.id.toLong(),
        gitlabProject.name,
        gitlabProject.description,
        visibility,
        gitlabProject.forksCount,
        gitlabProject.starCount,
        gitlabProject.tagList,
        Instant.ofEpochMilli(gitlabProject.createdAt.time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate(),
        Instant.ofEpochMilli(gitlabProject.lastActivityAt.time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate(),
        Commits(Timeline()),
        Issues(),
        readme = readme,
        contributingGuide = Documentation.notExistent(),
        license = license,
        ciDefinition = Documentation.notExistent()
    )
}