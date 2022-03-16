package com.roche.ambassador.indexing.project

import com.roche.ambassador.indexing.*
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.project.ProjectFilter
import io.micrometer.core.instrument.LongTaskTimer
import io.micrometer.core.instrument.MeterRegistry

internal class MeteredProjectIndexer(
    private val meterRegistry: MeterRegistry,
    private val delegate: ProjectIndexer
) : ProjectIndexer by delegate {

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
        val context = ProjectMeter(meterRegistry)
        delegate.indexAll(
            filter,
            context.wrapOnStarted(onStarted),
            context.wrapOnFinished(onFinished),
            context.wrapOnError(onError),
            context.wrapOnObjectIndexingStarted(onObjectIndexingStarted),
            context.wrapOnObjectExcludedByCriteria(onObjectExcludedByCriteria),
            context.wrapOnObjectIndexingError(onObjectIndexingError),
            context.wrapOnObjectIndexingFinished(onObjectIndexingFinished),
        )
    }

    private fun ProjectMeter.wrapOnObjectIndexingFinished(onObjectIndexingFinished: ObjectIndexingFinishedCallback<Project>): ObjectIndexingFinishedCallback<Project> = {
        stop(it)
        onObjectIndexingFinished(it)
    }

    private fun ProjectMeter.wrapOnObjectIndexingError(onObjectIndexingError: ObjectIndexingErrorCallback<Project>): ObjectIndexingErrorCallback<Project> = { error, project ->
        stop(project)
        recordError(error)
        onObjectIndexingError(error, project)
    }

    private fun ProjectMeter.wrapOnObjectExcludedByCriteria(onObjectExcludedByCriteria: ObjectExcludedByCriteriaCallback<Project>): ObjectExcludedByCriteriaCallback<Project> =
        { criteria, project ->
            recordExclusion(criteria)
            onObjectExcludedByCriteria(criteria, project)
        }

    private fun ProjectMeter.wrapOnObjectIndexingStarted(onObjectIndexingStarted: ObjectIndexingStartedCallback<Project>): ObjectIndexingStartedCallback<Project> = {
        start(it)
        onObjectIndexingStarted(it)
    }

    private fun ProjectMeter.wrapOnError(onError: IndexingErrorCallback): IndexingErrorCallback = {
        recordError(it)
        onError(it)
    }

    private fun ProjectMeter.wrapOnFinished(onFinished: IndexingFinishedCallback): IndexingFinishedCallback = {
        stop()
        onFinished()
    }

    private fun ProjectMeter.wrapOnStarted(onStarted: IndexingStartedCallback): IndexingStartedCallback = {
        start()
        onStarted()
    }

    private class ProjectMeter(private val meterRegistry: MeterRegistry) {

        private val projectTimer = LongTaskTimer.builder("project.indexing.time")
            .description("Time spent on processing projects measured individually per project")
            .register(meterRegistry)
        private var globalTimerSample: LongTaskTimer.Sample? = null
        private val projectSamples: MutableMap<Long, LongTaskTimer.Sample> = mutableMapOf()

        private fun increment(name: String, vararg tags: String?) {
            meterRegistry.counter(name, *tags).increment()
        }

        fun start(project: Project) {
            projectSamples[project.id] = projectTimer.start()
        }

        fun stop(project: Project) {
            projectSamples.remove(project.id)?.stop()
        }

        fun recordError(throwable: Throwable) {
            increment("project.indexing.errors", "error", throwable::class.simpleName)
        }

        fun recordExclusion(criteria: List<IndexingCriterion>) {
            criteria
                .map { it.name }
                .forEach {
                    increment("project.indexing.exclusions", "criterion", it)
                }
        }

        fun start() {
            globalTimerSample = LongTaskTimer.builder("project.indexing.total.time")
                .description("Total time spent on processing projects")
                .register(meterRegistry).start()
        }

        fun stop() {
            globalTimerSample?.stop()
        }
    }

}