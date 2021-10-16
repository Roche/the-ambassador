package com.filipowm.ambassador.project

import com.filipowm.ambassador.commons.api.Paged
import com.filipowm.ambassador.exceptions.Exceptions
import com.filipowm.ambassador.model.project.Project
import com.filipowm.ambassador.model.project.Visibility
import com.filipowm.ambassador.storage.project.ProjectEntityRepository
import com.filipowm.ambassador.storage.project.ProjectHistoryRepository
import com.filipowm.ambassador.storage.project.ProjectSearchRepository
import com.filipowm.ambassador.storage.project.SearchQuery
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@CacheConfig(cacheNames = ["projects"])
@Transactional(readOnly = true)
open class ProjectService(
    private val projectEntityRepository: ProjectEntityRepository,
    private val projectSearchRepository: ProjectSearchRepository,
    private val projectHistoryRepository: ProjectHistoryRepository
) {

    private val log = LoggerFactory.getLogger(ProjectService::class.java)

    @Cacheable(key = "#id.toString()") // TODO use CacheMono to enable reactive caching
    suspend fun getProject(id: Long): Project? {
        log.info("Retrieving project $id")
        return projectEntityRepository.findById(id)
            .map { it.project }
            .orElseThrow { Exceptions.NotFoundException("Project $id not found") }
    }

    suspend fun search(query: ListProjectsQuery, pageable: Pageable): Paged<SimpleProjectDto> {
        log.debug("Searching for project with query: {}", query)
        val q = SearchQuery(query.query, query.visibility.orElse(Visibility.INTERNAL))
        val result = projectSearchRepository.search(q, pageable)
            .map { SimpleProjectDto.from(it.project!!) }
        return Paged.from(result)
    }

    suspend fun getProjectHistory(id: Long, pageable: Pageable): Paged<ProjectHistoryDto> {
        val history = projectHistoryRepository.findByProjectId(id, pageable)
            .map { ProjectHistoryDto.from(it) }
        return Paged.from(history)
    }
}
