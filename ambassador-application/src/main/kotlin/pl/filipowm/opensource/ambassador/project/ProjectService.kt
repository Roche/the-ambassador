package pl.filipowm.opensource.ambassador.project

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.filipowm.opensource.ambassador.commons.exceptions.NotFoundException
import pl.filipowm.opensource.ambassador.model.Project
import pl.filipowm.opensource.ambassador.model.ProjectFilter
import pl.filipowm.opensource.ambassador.model.ProjectRepository
import pl.filipowm.opensource.ambassador.storage.ProjectEntity
import pl.filipowm.opensource.ambassador.storage.ProjectEntityRepository
import reactor.core.publisher.Mono

@Service
@CacheConfig(cacheNames = ["projects"])
open class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectEntityRepository: ProjectEntityRepository
) {

    private val log = LoggerFactory.getLogger(ProjectService::class.java)

    @Transactional(readOnly = true)
    @Cacheable(key = "#id.toString()") // TODO use CacheMono to enable reactive caching
    open fun getProject(id: Long): Mono<Project?> {
        log.info("Retrieving project $id")
        return projectEntityRepository.findById(id)
            .map { Mono.justOrEmpty(it.project) }
            .orElseThrow { NotFoundException("Project $id not found") }
    }

    @Transactional(readOnly = false)
    @CachePut(key = "#id.toString()") // TODO use CacheMono to enable reactive caching
    open fun reindex(id: Long): Mono<Project?> {
        log.info("Reindexing project $id")
        return Mono.fromCallable {
            projectRepository.getById(id.toString()).orElseThrow { NotFoundException("Project $id not found") }
        }
            .map { ProjectEntity.from(it) }
            .doOnNext { projectEntityRepository.save(it) }
            .doOnNext { log.info("Project {} (id={}) reindexed", it.name, it.id) }
            .map(ProjectEntity::project)
    }

    @Transactional(readOnly = false)
    open fun reindex() {
        val filter = ProjectFilter.internal()
        projectRepository.list(filter)
            .map { ProjectEntity.from(it) }
            .subscribe {
                projectEntityRepository.save(it)
                log.info("Project {} (id={}) indexed", it.name, it.id)
            }
    }

    open fun list(pageable: Pageable): Page<SimpleProjectDto> {
        return projectEntityRepository.findAll(pageable)
            .map { SimpleProjectDto.from(it.project!!) }
    }
}