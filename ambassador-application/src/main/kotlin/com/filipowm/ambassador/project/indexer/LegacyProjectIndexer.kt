package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.ConcurrencyProvider
import com.filipowm.ambassador.configuration.source.ProjectSourcesProperties
import com.filipowm.ambassador.exceptions.Exceptions
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.gitlab.GitLabSource
import com.filipowm.ambassador.model.Project
import com.filipowm.ambassador.model.ProjectFilter
import com.filipowm.ambassador.storage.ProjectEntity
import com.filipowm.ambassador.storage.ProjectEntityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import com.filipowm.gitlab.api.project.model.Project as GitLabProject

internal open class LegacyProjectIndexer(
    private val source: GitLabSource,
    private val projectEntityRepository: ProjectEntityRepository,
    private val concurrencyProvider: ConcurrencyProvider,
    private val projectSourceProperties: ProjectSourcesProperties
) : ProjectIndexer {

    companion object {
        private val log by LoggerDelegate()
    }

    private val indexingSemaphore = TimelyBlockingSemaphore(1, 3000)

    @Transactional(readOnly = false)
    override suspend fun indexOne(id: Long): Project {
        log.info("Reindexing project $id")
        return source.getById(id.toString())
            .map { ProjectEntity.from(it) }
            .map { projectEntityRepository.save(it) }
            .map { it.project }
            .orElseThrow { Exceptions.NotFoundException("Project $id not found") }!!
    }

    override suspend fun indexAll() {
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
                source.flow(filter)
                    .buffer()
                    .onEach { indexingSemaphore.touch() }
                    .filter { it.hasRepositorySetUp() }
                    .filter { isProjectWithinIndexingPeriod(it) }
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

    private fun isProjectWithinIndexingPeriod(gitlabProject: GitLabProject): Boolean {
        // TODO make this calculation directly in db to improve performance
        val shouldBeIndexed = projectEntityRepository.findById(gitlabProject.id!!.toLong())
            .filter { it.wasIndexedBefore(LocalDateTime.now().minus(projectSourceProperties.indexEvery)) }
            .isEmpty
        if (!shouldBeIndexed) {
            log.info("Project '{}' (id={}) was indexed recently and does not need to be reindex now. Skipping...", gitlabProject.nameWithNamespace, gitlabProject.id)
        }
        return shouldBeIndexed
    }

    private suspend fun index(gitlabProject: GitLabProject) {
        indexingSemaphore.touch()
        Optional.ofNullable(source.map(gitlabProject))
            .map { ProjectEntity.from(it) }
            .ifPresent {
                projectEntityRepository.save(it)
            }
    }

    private fun GitLabProject.hasRepositorySetUp(): Boolean {
        val hasRepo = this.defaultBranch != null
        if (!hasRepo) {
            log.warn("Project '{}' (id={}) does not have repo set up. Skipping indexing.", this.nameWithNamespace, this.id)
        }
        return hasRepo
    }
}
