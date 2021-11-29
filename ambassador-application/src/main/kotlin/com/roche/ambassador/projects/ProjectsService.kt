package com.roche.ambassador.projects

import com.roche.ambassador.commons.api.Paged
import com.roche.ambassador.exceptions.Exceptions
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.storage.project.ProjectEntityRepository
import com.roche.ambassador.storage.project.ProjectHistoryRepository
import com.roche.ambassador.storage.project.ProjectSearchQuery
import com.roche.ambassador.storage.project.ProjectSearchRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@CacheConfig(cacheNames = ["projects"])
@Transactional(readOnly = true)
open class ProjectsService(
    private val projectEntityRepository: ProjectEntityRepository,
    private val projectSearchRepository: ProjectSearchRepository,
    private val projectHistoryRepository: ProjectHistoryRepository
) {

    companion object {
        private val log by LoggerDelegate()
    }

    @Cacheable(key = "#id.toString()") // TODO use CacheMono to enable reactive caching
    suspend fun getProject(id: Long): Project? {
        log.debug("Retrieving project $id")
        return projectEntityRepository.findById(id)
            .map { it.project }
            .orElseThrow { Exceptions.NotFoundException("Project $id not found") }
    }

    suspend fun search(query: ListProjectsQuery, pageable: Pageable): Paged<SimpleProjectDto> {
        log.debug("Searching for project with query: {}", query)
        val q = ProjectSearchQuery(
            query.query,
            query.visibility.orElse(Visibility.INTERNAL),
            query.language.orElse(null),
            query.tags
        )
        val result = projectSearchRepository.search(q, pageable)
            .map { SimpleProjectDto.from(it.project) }
        return Paged.from(result)
    }

    suspend fun getProjectHistory(id: Long, pageable: Pageable): Paged<ProjectHistoryDto> {
        val history = projectHistoryRepository.findByProjectId(id, pageable)
            .map { ProjectHistoryDto.from(it) }
        return Paged.from(history)
    }
}
