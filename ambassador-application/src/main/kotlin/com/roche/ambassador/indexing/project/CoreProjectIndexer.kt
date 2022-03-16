package com.roche.ambassador.indexing.project

import com.roche.ambassador.ConcurrencyProvider
import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.exceptions.Exceptions
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.*
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.project.ProjectFilter
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.storage.indexing.Indexing
import com.roche.ambassador.storage.indexing.IndexingStatus
import com.roche.ambassador.storage.project.ProjectEntityRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

internal class CoreProjectIndexer internal constructor(
    private val source: ProjectSource,
    private val projectEntityRepository: ProjectEntityRepository,
    concurrencyProvider: ConcurrencyProvider,
    private val indexerProperties: IndexerProperties,
    private val indexingCriteria: IndexingCriteria,
    private val indexing: Indexing,
    private val continuation: Continuation,
    private val chain: IndexingChain,
) : ProjectIndexer {

    private val status: AtomicReference<IndexingStatus> = AtomicReference(IndexingStatus.IN_PROGRESS)
    private val producerScope = CoroutineScope(concurrencyProvider.getSourceProjectProducerDispatcher())
    private val consumerScope = CoroutineScope(concurrencyProvider.getIndexingConsumerDispatcher() + SupervisorJob())
    private val projectToIndexCount = AtomicInteger(0)
    private val finished = AtomicBoolean(false)
    private val sourceFinishedProducing = AtomicBoolean(false)

    companion object {
        private val log by LoggerDelegate()
    }

    private suspend fun processWithChain(project: Project): IndexingContext {
        val context = IndexingContext(project, source, consumerScope, null, null, indexerProperties, indexing, continuation)
        chain.accept(context)
        return context
    }

    override suspend fun indexOne(id: Long): Project {
        log.info("Indexing project $id regardless of criteria")
        val project = source.getById(id.toString())
        if (project.isPresent) {
            val processed = processWithChain(project.get())
            return processed.project
        }
        throw Exceptions.NotFoundException("Project $id not found")
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    override suspend fun indexAll(
        filter: ProjectFilter,
        onStarted: IndexingStartedCallback,
        onFinished: IndexingFinishedCallback,
        onError: IndexingErrorCallback,
        onObjectIndexingStarted: ObjectIndexingStartedCallback<Project>,
        onObjectExcludedByCriteria: ObjectExcludedByCriteriaCallback<Project>,
        onObjectIndexingError: ObjectIndexingErrorCallback<Project>,
        onObjectIndexingFinished: ObjectIndexingFinishedCallback<Project>
    ) {
        producerScope.launch {
            supervisorScope {
                log.info("Indexing started on {} with source filter {}", source.name(), filter)
                log.info("Criteria used: {}", indexingCriteria.getAllCriteriaNames())
                onStarted()
                source.flow(filter)
                    .buffer(1000)
                    .filter { it.isProjectWithinGracePeriod() }
                    .onEach { onObjectIndexingStarted(it) }
                    .filter { filterByCriteria(it, onObjectExcludedByCriteria) }
                    .onEach { projectToIndexCount.incrementAndGet() }
                    .onCompletion {
                        log.info("Finished producing projects to index from source")
                        sourceFinishedProducing.set(true)
                        tryFinish(onFinished)
                    }
                    .catch {
                        status.set(IndexingStatus.FAILED)
                        log.error("Failed processing projects", it)
                        sourceFinishedProducing.set(true)
                        onError(it)
                        tryFinish(onFinished)
                    }
                    .collect { index(it, onObjectIndexingFinished, onObjectIndexingError, onFinished) }
            }
        }
    }

    private suspend fun index(
        project: Project,
        onProjectIndexingFinished: ObjectIndexingFinishedCallback<Project>,
        onProjectIndexingError: ObjectIndexingErrorCallback<Project>,
        onFinished: IndexingFinishedCallback
    ) {
        consumerScope.launch {
            try {
                log.info("Indexing project '{}' (id={})", project.name, project.id)
                processWithChain(project)
                onProjectIndexingFinished(project)
            } catch (e: Throwable) {
                log.error("Failed while indexing project '{}' (id={}): {}", project.name, project.id, e.message, e)
                onProjectIndexingError(e, project)
            } finally {
                projectToIndexCount.decrementAndGet()
                tryFinish(onFinished)
            }
        }
    }

    private fun filterByCriteria(
        project: Project,
        onProjectExcludedByCriteria: ObjectExcludedByCriteriaCallback<Project>
    ): Boolean {
        val result = indexingCriteria.evaluate(project)
        if (result.failure) {
            onProjectExcludedByCriteria(result.failedCriteria, project)
        }
        return result.success
    }

    override fun forciblyStop(terminateImmediately: Boolean) {
        status.set(IndexingStatus.CANCELLED)
        val cause = IndexingForciblyStoppedException("Indexing forcibly stopped on demand")
        producerScope.cancel("Forcibly cancelled reading from source", cause)
        if (terminateImmediately) {
            consumerScope.cancel("Forcibly terminated all indexing in progress", cause)
        } else {
            log.warn("Waiting for projects in progress to finish indexing")
        }
    }

    private fun tryFinish(onFinished: IndexingFinishedCallback) {
        if (projectToIndexCount.get() == 0 &&
            sourceFinishedProducing.get() &&
            finished.compareAndSet(false, true)
        ) {
            status.compareAndSet(IndexingStatus.IN_PROGRESS, IndexingStatus.FINISHED)
            onFinished()
            log.info("Indexing of projects has finished")
        }
    }

    private fun Project.isProjectWithinGracePeriod(): Boolean {
        // TODO make this calculation directly in db to improve performance
        val shouldBeIndexed = projectEntityRepository.findById(id)
            .filter { !it.wasIndexedBefore(LocalDateTime.now().minus(indexerProperties.gracePeriod)) }
            .isEmpty
        if (!shouldBeIndexed) {
            log.info("Project '{}' (id={}) was indexed recently and is within grace period. Skipping...", name, id)
        }
        return shouldBeIndexed
    }

    override fun getStatus(): IndexingStatus = status.get()
}