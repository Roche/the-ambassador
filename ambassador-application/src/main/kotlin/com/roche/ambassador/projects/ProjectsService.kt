package com.roche.ambassador.projects

import com.roche.ambassador.commons.api.Paged
import com.roche.ambassador.exceptions.Exceptions
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.files.DocumentType
import com.roche.ambassador.model.files.RawFile
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.source.ProjectSources
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
    private val projectHistoryRepository: ProjectHistoryRepository,
    private val projectSources: ProjectSources
) {

    companion object {
        private val log by LoggerDelegate()
    }

    @Cacheable(key = "#id.toString()") // TODO use CacheMono to enable reactive caching
    suspend fun getProject(id: Long): Project {
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
            query.topics
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

    suspend fun getDocument(id: Long, documentType: DocumentType): DocumentDto {
        val project = getProject(id)
        val source = projectSources.get("gitlab") // TODO remove fixed value!
            .orElseThrow()
        val path = project.features.find(documentType.feature)
            .filter { it.exists() }
            .map { it.value().get().url!! }
            .orElseThrow { Exceptions.NotFoundException("Document %s does not exist".format(documentType.name)) }
        val file = source.readFile(id.toString(), path, project.defaultBranch!!)
            .filter { it.exists } // document might have been removed after last indexing
            .orElseThrow { Exceptions.NotFoundException("Document %s does not exist".format(documentType.name)) }
        return file.toDocument()
    }

    private fun RawFile.toDocument(): DocumentDto {
        return DocumentDto(this.content!!, this.contentLength!!)
    }
}
