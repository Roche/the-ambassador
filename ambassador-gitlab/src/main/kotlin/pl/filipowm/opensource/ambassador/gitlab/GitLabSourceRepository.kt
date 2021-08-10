package pl.filipowm.opensource.ambassador.gitlab

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.gitlab4j.api.GitLabApi
import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(GitLabSourceRepository::class.java)

    override suspend fun getById(id: String): Optional<Project> {
        return gitlabApi.projectApi
            .getOptionalProject(id, true, true, true)
            .map(gitLabProjectMapper::mapGitLabProjectToOpenSourceProject)
    }

    override suspend fun getByPath(path: String): Optional<Project> {
        return getById(path)
    }

    override suspend fun flow(filter: ProjectFilter): Flow<GitLabProject> {
        val glFilter = org.gitlab4j.api.models.ProjectFilter()
            .withArchived(filter.archived)
            .withStatistics(true)
            .withCustomAttributes(false)
            .withIdAfter(20000)
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