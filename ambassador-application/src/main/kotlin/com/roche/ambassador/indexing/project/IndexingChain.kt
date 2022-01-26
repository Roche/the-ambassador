package com.roche.ambassador.indexing.project

import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.indexing.project.steps.IndexingStep
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.source.ProjectSource
import kotlinx.coroutines.CoroutineScope

class IndexingChain(
    private val steps: List<IndexingStep>,
    private val source: ProjectSource,
    private val coroutineScope: CoroutineScope,
    private val config: IndexerProperties
) {

    suspend fun accept(project: Project): IndexingContext {
        val firstStep = steps[0]
        val context = IndexingContext(project, source, coroutineScope, firstStep::class, null, config)

        firstStep.handle(context)
        return context
    }
}
