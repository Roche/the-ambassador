package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.project.IndexingChain
import com.roche.ambassador.indexing.project.IndexingContext
import org.springframework.stereotype.Component

@Component
internal class ContinuationVerificationStep : IndexingStep {

    companion object {
        private val log by LoggerDelegate()
    }

    override suspend fun handle(context: IndexingContext, chain: IndexingChain) {
        if (context.entity?.lastIndexingId in context.continuation.unfinishedIndexingIds) {
            log.info("Project '{}' (id={}) was indexed in {} and it does not need to be reindexed.", context.project.fullName, context.project.id, context.entity!!.lastIndexingId)
        } else {
            chain.accept(context)
        }
    }
}