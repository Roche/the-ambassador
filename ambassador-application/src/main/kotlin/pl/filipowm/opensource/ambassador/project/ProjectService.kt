package pl.filipowm.opensource.ambassador.project

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.filipowm.opensource.ambassador.commons.api.Paged
import pl.filipowm.opensource.ambassador.commons.exceptions.NotFoundException
import pl.filipowm.opensource.ambassador.model.Project
import pl.filipowm.opensource.ambassador.model.Visibility
import pl.filipowm.opensource.ambassador.storage.ProjectEntityRepository
import pl.filipowm.opensource.ambassador.storage.ProjectSearchRepository
import pl.filipowm.opensource.ambassador.storage.SearchQuery

@Service
@CacheConfig(cacheNames = ["projects"])
open class ProjectService(
    private val projectEntityRepository: ProjectEntityRepository,
    private val projectSearchRepository: ProjectSearchRepository
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

    @Transactional(readOnly = true)
    open suspend fun list(query: ListProjectsQuery, pageable: Pageable): Paged<SimpleProjectDto> {
        log.debug("Searching for project with query: {}", query)
        val q = SearchQuery(query.name, query.visibility.orElse(Visibility.INTERNAL))
        val result = projectSearchRepository.search(q, pageable)
            .map { SimpleProjectDto.from(it.project!!) }
        return Paged.from(result)
    }
}