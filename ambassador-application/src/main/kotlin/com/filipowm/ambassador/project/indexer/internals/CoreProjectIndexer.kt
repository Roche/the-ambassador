package com.filipowm.ambassador.project.indexer.internals

import com.filipowm.ambassador.ConcurrencyProvider
import com.filipowm.ambassador.exceptions.Exceptions
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.model.ScorecardCalculator
import com.filipowm.ambassador.model.feature.FeatureReaders
import com.filipowm.ambassador.model.project.Project
import com.filipowm.ambassador.model.project.ProjectFilter
import com.filipowm.ambassador.model.project.Visibility
import com.filipowm.ambassador.model.score.ActivityScorePolicy
import com.filipowm.ambassador.model.source.ProjectSource
import com.filipowm.ambassador.project.indexer.*
import com.filipowm.ambassador.storage.project.ProjectEntity
import com.filipowm.ambassador.storage.project.ProjectEntityRepository
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
    private val finished = AtomicBoolean(false)
    private val sourceFinishedProducing = AtomicBoolean(false)

    companion object {
        private val log by LoggerDelegate()
    }

    private suspend fun readFeatures(project: Project) {
        FeatureReaders.all()
            .map {
                consumerScope.async {
                    project.readFeature(it, source)
                }
            }.awaitAll()
    }

    override suspend fun indexOne(id: Long): Project {
        log.info("Reindexing project $id")
        val project = source.getById(id.toString())
        if (project.isPresent) {
            readFeatures(project.get())
            val scorecard = ScorecardCalculator(
                setOf(
                    ActivityScorePolicy
                )
            ).calculateFor(project.get())
            project.get().scorecard = scorecard
        }
        return project
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
        onProjectExcludedByCriteria: ProjectExcludedByCriteriaCallback,
        onProjectIndexingError: ProjectIndexingErrorCallback,
        onProjectIndexingFinished: ProjectIndexingFinishedCallback
    ) {
        val lastActivityAfterHalfYear = LocalDateTime.now().minusDays(183)
        val filter = ProjectFilter(Visibility.INTERNAL, false, lastActivityAfterHalfYear)
        producerScope.launch {
            supervisorScope {
                log.info("Indexing started on {} with source filter {}", source.name(), filter)
                onStarted()
                source.flow(filter)
                    .buffer(1000)
                    .filter { it.isProjectWithinIndexingPeriod() }
                    .onEach { onProjectIndexingStarted(it) }
                    .filter {
                        val result = indexingCriteria.evaluate(it)
                        if (result.failure) {
                            onProjectExcludedByCriteria(result.failedCriteria, it)
                        }
                        result.success
                    }
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
                                log.info("Indexing project '{}' (id={})", name, id)
                                val projectToSave = Optional.ofNullable(source.map(it))

                                if (projectToSave.isPresent) {
                                    val project = projectToSave.get()
                                    readFeatures(project)
                                    val scorecard = ScorecardCalculator(
                                        setOf(
                                            ActivityScorePolicy
                                        )
                                    ).calculateFor(project)
                                    project.scorecard = scorecard
                                    val entity = ProjectEntity.from(project)
                                    projectEntityRepository.save(entity)
                                    onProjectIndexingFinished(project)
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

    override fun forciblyStop(terminateImmediately: Boolean) {
        val cause = IndexingForciblyStoppedException("Indexing forcibly stopped on demand")
        producerScope.cancel("Forcibly cancelled reading from source", cause)
        if (terminateImmediately) {
            consumerScope.cancel("Forcibly terminated all indexing in progress", cause)
        } else {
            log.warn("Waiting for projects in progress to finish indexing")
        }
    }

    override fun getSource(): ProjectSource<Any> = source

    private fun tryFinish(onFinished: IndexingFinishedCallback) {
        if (projectToIndexCount.get() == 0 &&
            sourceFinishedProducing.get() &&
            finished.compareAndSet(false, true)
        ) {
            onFinished()
            log.info("Indexing of projects has finished")
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
