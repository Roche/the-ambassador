package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.ConcurrencyProvider
import com.filipowm.ambassador.exceptions.Exceptions
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.model.Project
import com.filipowm.ambassador.model.ProjectFilter
import com.filipowm.ambassador.model.criteria.IndexingCriteria
import com.filipowm.ambassador.model.source.ProjectSource
import com.filipowm.ambassador.storage.ProjectEntity
import com.filipowm.ambassador.storage.ProjectEntityRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

internal class CoreProjectIndexer(
    private val source: ProjectSource<Any>,
    private val projectEntityRepository: ProjectEntityRepository,
    concurrencyProvider: ConcurrencyProvider,
    private val indexEvery: Duration,
    private val indexingCriteria: IndexingCriteria<Any>
) : ProjectIndexer {

    private val producerScope = CoroutineScope(concurrencyProvider.getSourceProjectProducerDispatcher())
    private val consumerScope = CoroutineScope(concurrencyProvider.getIndexingConsumerDispatcher() + SupervisorJob())
    private val projectToIndexCount = AtomicInteger(0)
    private val totalIndexedCount = AtomicInteger(0)
    private val finished = AtomicBoolean(false)
    private val sourceFinishedProducing = AtomicBoolean(false)

    companion object {
        private val log by LoggerDelegate()
    }

    override suspend fun indexOne(id: Long): Project {
        log.info("Reindexing project $id")
        return source.getById(id.toString())
            .map { ProjectEntity.from(it) }
            .map { projectEntityRepository.save(it) }
            .map { it.project }
            .orElseThrow { Exceptions.NotFoundException("Project $id not found") }!!
    }

    override suspend fun indexAll(
        onStarted: IndexingStartedCallback,
        onFinished: IndexingFinishedCallback,
        onError: IndexingErrorCallback,
        onProjectIndexingStarted: ProjectIndexingStartedCallback,
        onProjectIndexingError: ProjectIndexingErrorCallback,
        onProjectIndexingFinished: ProjectIndexingFinishedCallback
    ) {
        val filter = ProjectFilter.internal()
        producerScope.launch {
            supervisorScope {
                log.info("Indexing started on {}", source.getName())
                onStarted()
                source.flow(filter)
                    .buffer(1000)
                    .filter { it.isProjectWithinIndexingPeriod() }
                    .filter { indexingCriteria.evaluate(it) }
                    .onEach { projectToIndexCount.incrementAndGet() }
                    .onCompletion {
                        log.info("Finished producing projects to index from source")
                        sourceFinishedProducing.set(true)
                        tryFinish(onFinished)
                    }
                    .catch {
                        log.error("Failed processing project", it)
                        sourceFinishedProducing.set(true)
                        onError(it)
                        tryFinish(onFinished)
                    }
                    .collect {
                        consumerScope.launch {
                            val name = source.resolveName(it)
                            val id = source.resolveId(it)
                            try {
                                onProjectIndexingStarted(it)
                                log.info("Indexing project '{}' (id={})", name, id)
                                val projectToSave = Optional.ofNullable(source.map(it))
                                if (projectToSave.isPresent) {
                                    val entity = ProjectEntity.from(projectToSave.get())
                                    projectEntityRepository.save(entity)
                                    totalIndexedCount.incrementAndGet()
                                    onProjectIndexingFinished(projectToSave.get())
                                    log.info("Indexed project '{}' (id={})", name, id)
                                } else {
                                    log.warn("Project '{}' (id={}) not indexed, because it could not be analyzed", name, id)
                                }
                            } catch (e: Throwable) {
                                log.error("Failed while indexing project '{}' (id={}): {}", name, id, e.message)
                                onProjectIndexingError(e, it)
                            } finally {
                                projectToIndexCount.decrementAndGet()
                                tryFinish(onFinished)
                            }
                        }
                    }
            }
        }
    }

    override fun forciblyStop() {
        producerScope.cancel("Forcibly cancelled producer")
        consumerScope.cancel("Forcibly cancelled consumer")
    }

    private fun tryFinish(onFinished: IndexingFinishedCallback) {
        if (projectToIndexCount.get() == 0
            && sourceFinishedProducing.get()
            && finished.compareAndSet(false, true)) {
            onFinished()
            log.info("Indexing of projects has finished. Indexed {} projects", totalIndexedCount.get())
            totalIndexedCount.set(0)
        }
    }

    private fun Any.isProjectWithinIndexingPeriod(): Boolean {
        // TODO make this calculation directly in db to improve performance
        val id = source.resolveId(this)
        val shouldBeIndexed = projectEntityRepository.findById(id.toLong())
            .filter { !it.wasIndexedBefore(LocalDateTime.now().minus(indexEvery)) }
            .isEmpty
        if (!shouldBeIndexed) {
            log.info("Project '{}' (id={}) was indexed recently and does not need to be reindex now. Skipping...", source.resolveName(this), source.resolveId(this))
        }
        return shouldBeIndexed
    }
}