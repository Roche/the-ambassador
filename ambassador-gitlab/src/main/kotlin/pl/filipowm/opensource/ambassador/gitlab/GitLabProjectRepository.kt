package pl.filipowm.opensource.ambassador.gitlab

import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.gitlab4j.api.GitLabApi
import org.slf4j.LoggerFactory
import pl.filipowm.opensource.ambassador.ConcurrencyProvider
import pl.filipowm.opensource.ambassador.model.Project
import pl.filipowm.opensource.ambassador.model.ProjectFilter
import pl.filipowm.opensource.ambassador.model.ProjectRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.ParallelFlux
import java.util.*
import org.gitlab4j.api.models.Project as GitLabProject
import org.gitlab4j.api.models.ProjectFilter as GitLabProjectFilter

@ObsoleteCoroutinesApi
class GitLabProjectRepository(
    private val gitlabApi: GitLabApi,
    private val projectMapper: ProjectMapper,
    private val concurrencyProvider: ConcurrencyProvider
) : ProjectRepository {

    private val log = LoggerFactory.getLogger(GitLabProjectRepository::class.java)

    override fun getById(id: String): Optional<Project> {
        return gitlabApi.projectApi
            .getOptionalProject(id, true, true, true)
            .map(projectMapper::mapGitLabProjectToOpenSourceProject)
    }

    override fun getByPath(path: String): Optional<Project> {
        return getById(path)
    }

    override fun list(filter: ProjectFilter): ParallelFlux<Project> {
        val glFilter = GitLabProjectFilter()
            .withArchived(filter.archived)
            .withStatistics(true)
            .withCustomAttributes(true)
            .withIdAfter(19300)
        if (filter.visibility != null) {
            glFilter.withVisibility(VisibilityMapper.fromAmbassador(filter.visibility!!))
        }
        return Flux.create<GitLabProject> {
            val pager = gitlabApi.projectApi.getProjects(glFilter, 50)
            while (pager.hasNext()) {
                log.info("Reading page {}/{}", pager.currentPage + 1, pager.totalPages)
                for (project in pager.next()) {
                    log.info("Publishing project {}", project.id)
                    it.next(project)
                }
            }
        }
            .parallel()
            .runOn(concurrencyProvider.getScheduler())
            .map(projectMapper::mapGitLabProjectToOpenSourceProject)
    }

    override fun save(project: Project) {
        TODO("Not yet implemented")
    }
}