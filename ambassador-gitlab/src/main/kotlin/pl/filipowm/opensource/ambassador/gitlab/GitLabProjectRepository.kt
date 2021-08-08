package pl.filipowm.opensource.ambassador.gitlab

import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.gitlab4j.api.GitLabApi
import org.glassfish.jersey.internal.guava.ThreadFactoryBuilder
import org.slf4j.LoggerFactory
import pl.filipowm.opensource.ambassador.model.Project
import pl.filipowm.opensource.ambassador.model.ProjectFilter
import pl.filipowm.opensource.ambassador.model.ProjectRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.ParallelFlux
import reactor.core.scheduler.Schedulers
import java.util.*
import java.util.concurrent.Executors
import org.gitlab4j.api.models.Project as GitLabProject
import org.gitlab4j.api.models.ProjectFilter as GitLabProjectFilter

@ObsoleteCoroutinesApi
class GitLabProjectRepository(
    private val gitlabApi: GitLabApi,
    private val projectMapper: ProjectMapper
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
        val tf = ThreadFactoryBuilder()
            .setNameFormat("project-list-%d")
            .build()
        return Flux.create<GitLabProject> {
            val pager = gitlabApi.projectApi.getProjects(glFilter, 50)
            while (pager.hasNext()) {
                log.info("Reading page {}/{}", pager.currentPage, pager.totalPages)
                for (project in pager.next()) {
                    log.debug("Publishing project {}", project.id)
                    it.next(project)
                }
            }
        }
            .parallel()
            .runOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(10, tf)))
//            .runOn(Schedulers.newParallel(20, tf))
//            .subscribeOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(200)))
            .map(projectMapper::mapGitLabProjectToOpenSourceProject)
    }

    override fun save(project: Project) {
        TODO("Not yet implemented")
    }
}