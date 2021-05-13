package pl.filipowm.innersource.ambassador.project

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.filipowm.innersource.ambassador.commons.exceptions.NotFoundException
import pl.filipowm.innersource.ambassador.model.Project
import pl.filipowm.innersource.ambassador.model.ProjectRepository
import pl.filipowm.innersource.ambassador.storage.ProjectEntity
import pl.filipowm.innersource.ambassador.storage.ProjectEntityRepository
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
            .map(ProjectEntity::project)
            .cache()
    }
}