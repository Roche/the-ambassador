package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.project.IndexingChain
import com.roche.ambassador.indexing.project.IndexingContext
import org.springframework.stereotype.Component

@Component
internal class SubscriptionVerificationStep : IndexingStep {

    companion object {
        private val log by LoggerDelegate()
    }

    override suspend fun handle(context: IndexingContext, chain: IndexingChain) {
        val unsubscribeTopic = context.config.subscription.unsubscribe.topic
        val unsubscribeFile = context.config.subscription.unsubscribe.filename
        if (unsubscribeTopic != null && unsubscribeTopic.lowercase() in context.project.topics.map { it.lowercase() }) {
            context.subscribed = false
        } else if (unsubscribeFile != null && context.project.defaultBranch != null) {
            context.subscribed = context.source
                .readFile(context.project.id.toString(), unsubscribeFile, context.project.defaultBranch!!)
                .isEmpty
        }
        if (!context.subscribed) {
            log.info("Project {} (id={}) is unsubscribed from indexing. It will be stored without features and will not be analyzed, but will not be available for read.", context.project.fullName, context.project.id)
        }
        chain.accept(context)
    }
}
