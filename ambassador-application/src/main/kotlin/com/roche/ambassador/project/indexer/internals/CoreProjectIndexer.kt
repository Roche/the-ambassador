package com.roche.ambassador.project.indexer.internals

import com.roche.ambassador.ConcurrencyProvider
import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.exceptions.Exceptions
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.ScorecardCalculator
import com.roche.ambassador.model.feature.FeatureReaders
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.project.ProjectFilter
import com.roche.ambassador.model.project.Visibility
import com.roche.ambassador.model.score.ActivityScorePolicy
import com.roche.ambassador.model.score.CriticalityScorePolicy
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.project.indexer.*
import com.roche.ambassador.storage.project.ProjectEntity
import com.roche.ambassador.storage.project.ProjectEntityRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

internal class CoreProjectIndexer(
    private val source: ProjectSource<Any>,
    private val projectEntityRepository: ProjectEntityRepository,
    concurrencyProvider: ConcurrencyProvider,
    private val indexerProperties: IndexerProperties,
    private val indexingCriteria: IndexingCriteria<Any>,
    private val transactionTemplate: TransactionTemplate
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
        log.info("Indexing project $id regardless of criteria")
        val project = source.getById(id.toString())
        if (project.isPresent) {
            return index(project.get()).project!!
        }
        throw Exceptions.NotFoundException("Project $id not found")
    }

    private suspend fun index(project: Project): ProjectEntity {
        readFeatures(project)
        val scorecard = ScorecardCalculator(
            setOf(
                ActivityScorePolicy,
                CriticalityScorePolicy
            )
        ).calculateFor(project)
        project.scorecard = scorecard
        return transactionTemplate.execute {
            val currentEntityOptional = projectEntityRepository.findById(project.id)
            val toSave: ProjectEntity = if (currentEntityOptional.isPresent) {
                val currentEntity = currentEntityOptional.get()
                currentEntity.removeHistoryToMatchLimit(indexerProperties.historySize - 1)
                currentEntity.snapshot()
                currentEntity.updateIndex(project)
                currentEntity
            } else {
                ProjectEntity.from(project)
            }
            val result = projectEntityRepository.save(toSave)
            log.info("Indexed project '{}' (id={})", project.name, project.id)
            result
        }!!
    }

    @SuppressWarnings("TooGenericExceptionCaught")
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
                                    index(project)
                                    onProjectIndexingFinished(project)
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
            .filter { !it.wasIndexedBefore(LocalDateTime.now().minus(indexerProperties.indexEvery)) }
            .isEmpty
        if (!shouldBeIndexed) {
            log.info("Project '{}' (id={}) was indexed recently and does not need to be reindex now. Skipping...", source.resolveName(this), source.resolveId(this))
        }
        return shouldBeIndexed
    }
}
