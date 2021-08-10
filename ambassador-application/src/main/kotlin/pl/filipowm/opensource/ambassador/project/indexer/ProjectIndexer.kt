package pl.filipowm.opensource.ambassador.project.indexer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pl.filipowm.opensource.ambassador.ConcurrencyProvider
import pl.filipowm.opensource.ambassador.commons.exceptions.NotFoundException
import pl.filipowm.opensource.ambassador.gitlab.GitLabSourceRepository
import pl.filipowm.opensource.ambassador.model.Project
import pl.filipowm.opensource.ambassador.model.ProjectFilter
import pl.filipowm.opensource.ambassador.project.ProjectService
import pl.filipowm.opensource.ambassador.storage.ProjectEntity
import pl.filipowm.opensource.ambassador.storage.ProjectEntityRepository
import java.util.*

@Component
internal open class ProjectIndexer(
    private val projectRepository: GitLabSourceRepository,
    private val projectEntityRepository: ProjectEntityRepository,
    private val concurrencyProvider: ConcurrencyProvider
) {

    private val log = LoggerFactory.getLogger(ProjectService::class.java)

    @Transactional(readOnly = false)
    open suspend fun reindex(id: Long): Project? {
        log.info("Reindexing project $id")
        return projectRepository.getById(id.toString())
            .map { ProjectEntity.from(it) }
            .map { projectEntityRepository.save(it) }
            .map { it.project }
            .orElseThrow { NotFoundException("Project $id not found") }
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