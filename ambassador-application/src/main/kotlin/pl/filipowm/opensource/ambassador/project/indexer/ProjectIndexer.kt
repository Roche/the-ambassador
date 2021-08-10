package pl.filipowm.opensource.ambassador.project.indexer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pl.filipowm.opensource.ambassador.ConcurrencyProvider
import pl.filipowm.opensource.ambassador.exceptions.Exceptions
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
    private val indexingSemaphore = TimelyBlockingSemaphore(1, 3000)

    @Transactional(readOnly = false)
    open suspend fun reindex(id: Long): Project? {
        log.info("Reindexing project $id")
        return projectRepository.getById(id.toString())
            .map { ProjectEntity.from(it) }
            .map { projectEntityRepository.save(it) }
            .map { it.project }
            .orElseThrow { Exceptions.NotFoundException("Project $id not found") }
    }

    open suspend fun reindex() {
        log.info("Starting indexing all projects within source repository")
        if (indexingSemaphore.tryAcquire()) {
            startIndexing()
        } else {
            log.warn("Indexing locked. May be unclocked in {}ms", indexingSemaphore.unlocksIn())
            throw IndexingAlreadyStartedException("Unable to start new projects indexing, because it is already running")
        }
    }

    private suspend fun startIndexing() {
        val filter = ProjectFilter.internal()
        val scope = CoroutineScope(concurrencyProvider.getCoroutineDispatcher())
        scope.launch {
            log.info("Indexing started")
            indexingSemaphore.touch()
            projectRepository.flow(filter)
                .onEach { indexingSemaphore.touch() }
//                .flowOn(concurrencyProvider.getCoroutineDispatcher())
                .filter { hasRepositorySetUp(it) }
                .collect {
                    launch {
                        indexingSemaphore.touch()
                        Optional.ofNullable(projectRepository.mapper().invoke(it))
                            .map { ProjectEntity.from(it) }
                            .ifPresent {
                                indexingSemaphore.touch()
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