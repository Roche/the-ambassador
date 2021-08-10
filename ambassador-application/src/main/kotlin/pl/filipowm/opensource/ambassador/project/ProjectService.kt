package pl.filipowm.opensource.ambassador.project

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.filipowm.opensource.ambassador.ConcurrencyProvider
import pl.filipowm.opensource.ambassador.commons.api.Paged
import pl.filipowm.opensource.ambassador.commons.exceptions.NotFoundException
import pl.filipowm.opensource.ambassador.gitlab.GitLabSourceRepository
import pl.filipowm.opensource.ambassador.model.Project
import pl.filipowm.opensource.ambassador.model.ProjectFilter
import pl.filipowm.opensource.ambassador.model.Visibility
import pl.filipowm.opensource.ambassador.storage.ProjectEntity
import pl.filipowm.opensource.ambassador.storage.ProjectEntityRepository
import pl.filipowm.opensource.ambassador.storage.ProjectSearchRepository
import pl.filipowm.opensource.ambassador.storage.SearchQuery
import java.util.*

@Service
@CacheConfig(cacheNames = ["projects"])
open class ProjectService(
    private val projectRepository: GitLabSourceRepository,
    private val projectEntityRepository: ProjectEntityRepository,
    private val projectSearchRepository: ProjectSearchRepository,
    private val concurrencyProvider: ConcurrencyProvider
) {

    private val log = LoggerFactory.getLogger(ProjectService::class.java)

    @Transactional(readOnly = true)
    @Cacheable(key = "#id.toString()") // TODO use CacheMono to enable reactive caching
    open suspend fun getProject(id: Long): Project? {
        log.info("Retrieving project $id")
        return projectEntityRepository.findById(id)
            .map { it.project }
            .orElseThrow { NotFoundException("Project $id not found") }
    }

    @Transactional(readOnly = false)
    @CachePut(key = "#id.toString()") // TODO use CacheMono to enable reactive caching
    open suspend fun reindex(id: Long): Project? {
        log.info("Reindexing project $id")
        return projectRepository.getById(id.toString())
            .map { ProjectEntity.from(it) }
            .map { projectEntityRepository.save(it) }
            .map { it.project }
            .orElseThrow { NotFoundException("Project $id not found") }
    }

    @Transactional(readOnly = true)
    open suspend fun list(query: ListProjectsQuery, pageable: Pageable): Paged<SimpleProjectDto> {
        log.debug("Searching for project with query: {}", query)
        val q = SearchQuery(query.name, query.visibility.orElse(Visibility.INTERNAL))
        val result = projectSearchRepository.search(q, pageable)
            .map { SimpleProjectDto.from(it.project!!) }
        return Paged.from(result)
    }

    open suspend fun reindex() {
        val filter = ProjectFilter.internal()
        val scope = CoroutineScope(concurrencyProvider.getCoroutineDispatcher())
        scope.launch {
            log.info("Indexing started")
            projectRepository.flow(filter)
//                .flowOn(concurrencyProvider.getCoroutineDispatcher())
                .filter { hasRepositorySetUp(it) }
                .collect {
                    launch {
                        Optional.ofNullable(projectRepository.mapper().invoke(it))
                            .map { ProjectEntity.from(it) }
                            .ifPresent {
                                projectEntityRepository.save(it)
                                log.info("Project {} (id={}) indexed", it.name, it.id)
                            }
                    }
                }
        }
    }

    private fun hasRepositorySetUp(it: org.gitlab4j.api.models.Project): Boolean {
        val hasRepo = it.defaultBranch != null && !it.emptyRepo
        if (!hasRepo) {
            log.warn("Project {} (id={}) does not have repo set up. Skipping indexing.", it.name, it.id)
        }
        return hasRepo
    }
}