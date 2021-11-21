package com.roche.ambassador.project.indexer.steps

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.source.ProjectSource
import kotlinx.coroutines.CoroutineScope

class IndexingChain(
    private val steps: List<IndexingStep>,
    private val source: ProjectSource,
    private val coroutineScope: CoroutineScope
) {

    companion object {
        private val log by LoggerDelegate()
    }

    suspend fun accept(project: Project): IndexingContext {
        val firstStep = steps[0]
        val context = IndexingContext(project, source, coroutineScope, firstStep::class)
        steps.forEach { it.handle(context) } // TODO this is not really a true chain, should be changed later when we have more steps
        return context
    }

}