package com.filipowm.ambassador.project.indexer

//import com.filipowm.ambassador.model.Project
import com.filipowm.ambassador.ConcurrencyProvider
import com.filipowm.ambassador.exceptions.Exceptions
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.gitlab.GitLabSourceRepository
import com.filipowm.ambassador.model.ProjectFilter
import com.filipowm.ambassador.storage.ProjectEntity
import com.filipowm.ambassador.storage.ProjectEntityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import com.filipowm.gitlab.api.project.model.Project as GitLabProject

@Component
internal open class ProjectIndexer(
    private val projectRepository: GitLabSourceRepository,
    private val projectEntityRepository: ProjectEntityRepository,
    private val concurrencyProvider: ConcurrencyProvider
) {

    companion object {
        private val log by LoggerDelegate()
    }

    private val indexingSemaphore = TimelyBlockingSemaphore(1, 3000)

    @Transactional(readOnly = false)
    open suspend fun reindex(id: Long): com.filipowm.ambassador.model.Project? {
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
        val producerScope = CoroutineScope(concurrencyProvider.getSourceProjectProducerDispatcher())
        val consumerScope = CoroutineScope(concurrencyProvider.getIndexingConsumerDispatcher() + SupervisorJob())
        producerScope.launch {
            supervisorScope {
                log.info("Indexing started")
                indexingSemaphore.touch()
                projectRepository.flow(filter)
                    .buffer()
                    .onEach { indexingSemaphore.touch() }
                    .filter { it.hasRepositorySetUp() }
                    .onCompletion { log.info("Finished producing projects to index from source") }
                    .catch { log.error("Failed processing project", it) }
                    .collect {
                        consumerScope.launch {
                            try {
                                log.info("Indexing project '{}' (id={})", it.nameWithNamespace, it.id)
                                index(it)
                                log.info("Indexed project '{}' (id={})", it.nameWithNamespace, it.id)
                            } catch (e: Throwable) {
                                log.error("Failed while indexing project '{}' (id={}): {}", it.nameWithNamespace, it.id, e.message)
                            }
                        }

                    }
            }
        }
    }

    private suspend fun index(gitlabProject: GitLabProject) {
        indexingSemaphore.touch()
        Optional.ofNullable(projectRepository.mapper().invoke(gitlabProject))
            .map { ProjectEntity.from(it) }
            .ifPresent {
                projectEntityRepository.save(it)
            }
    }

    private fun GitLabProject.hasRepositorySetUp(): Boolean {
        val hasRepo = this.defaultBranch != null
        if (!hasRepo) {
            log.warn("Project {} (id={}) does not have repo set up. Skipping indexing.", this.name, this.id)
        }
        return hasRepo
    }
}
