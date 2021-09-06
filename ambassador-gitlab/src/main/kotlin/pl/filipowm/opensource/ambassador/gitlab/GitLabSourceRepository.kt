package pl.filipowm.opensource.ambassador.gitlab

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.gitlab4j.api.GitLabApi
import pl.filipowm.opensource.ambassador.exceptions.Exceptions
import pl.filipowm.opensource.ambassador.extensions.LoggerDelegate
import pl.filipowm.opensource.ambassador.model.Project
import pl.filipowm.opensource.ambassador.model.ProjectFilter
import pl.filipowm.opensource.ambassador.model.ProjectMapper
import pl.filipowm.opensource.ambassador.model.SourceProjectRepository
import java.util.*
import org.gitlab4j.api.models.Project as GitLabProject

class GitLabSourceRepository(
    private val gitlabApi: GitLabApi,
    private val gitLabProjectMapper: GitLabProjectMapper
) : SourceProjectRepository<GitLabProject> {

    companion object {
        private val log by LoggerDelegate()
    }

    override suspend fun getById(id: String): Optional<Project> {
        log.info("Reading project {}", id)
        val prj = gitlabApi.projectApi
            .getOptionalProject(id, true, true, true)
            .orElseThrow { Exceptions.NotFoundException("Project with ID $id not found") }
        return Optional.ofNullable(gitLabProjectMapper.mapGitLabProjectToOpenSourceProject(prj))
    }

    override suspend fun getByPath(path: String): Optional<Project> {
        return getById(path)
    }

    override suspend fun flow(filter: ProjectFilter): Flow<GitLabProject> {
        val glFilter = org.gitlab4j.api.models.ProjectFilter()
            .withArchived(filter.archived)
            .withStatistics(true)
            .withCustomAttributes(false)
        if (filter.visibility != null) {
            glFilter.withVisibility(VisibilityMapper.fromAmbassador(filter.visibility!!))
        }
        return flow {
            val pager = gitlabApi.projectApi.getProjects(glFilter, 50)
            while (pager.hasNext()) {
                log.info("Reading page {}/{}", pager.currentPage + 1, pager.totalPages)
                for (project in pager.next()) {
                    log.debug("Publishing project {}", project.id)
                    emit(project)
                }
            }
        }
    }

    override fun mapper(): ProjectMapper<GitLabProject> = gitLabProjectMapper::mapGitLabProjectToOpenSourceProject
}
