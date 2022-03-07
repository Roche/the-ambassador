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
import com.roche.ambassador.storage.project.ProjectSearchQuery
import com.roche.ambassador.storage.project.ProjectSearchRepository
import com.roche.ambassador.storage.project.ProjectStatisticsHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@CacheConfig(cacheNames = ["projects"])
@Transactional(readOnly = true)
internal class ProjectsService(
    private val projectEntityRepository: ProjectEntityRepository,
    private val projectSearchRepository: ProjectSearchRepository,
    private val projectStatisticsHistoryRepository: ProjectStatisticsHistoryRepository,
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

    suspend fun getProjectStatsHistory(id: Long, query: ProjectStatsHistoryQuery): ProjectStatsHistoryDto {
        log.info("Reading project statistics history for project $id (query=$query)")
        val before = query.getBeforeAsLocalDateTime()
        val after = query.getAfterAsLocalDateTime()
        val history = withContext(Dispatchers.IO) {
            if (after.isPresent && before.isPresent) {
                projectStatisticsHistoryRepository.findByProjectIdAndDateBetween(id, after.get(), before.get())
            } else if (after.isPresent) {
                projectStatisticsHistoryRepository.findByProjectIdAndDateGreaterThanEqual(id, after.get())
            } else if (before.isPresent) {
                projectStatisticsHistoryRepository.findByProjectIdAndDateLessThan(id, before.get())
            } else {
                // by default read history from latest year
                projectStatisticsHistoryRepository.findByProjectIdAndDateGreaterThanEqual(id, LocalDateTime.now().minusYears(1))
            }
        }
        return ProjectStatsHistoryDto.from(id, history)
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
