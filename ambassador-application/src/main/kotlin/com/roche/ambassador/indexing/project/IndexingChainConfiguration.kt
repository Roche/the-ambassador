package com.roche.ambassador.indexing.project

import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.indexing.project.steps.*
import com.roche.ambassador.indexing.project.steps.ContinuationVerificationStep
import com.roche.ambassador.indexing.project.steps.SaveProjectStep
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class IndexingChainConfiguration(private val properties: IndexerProperties) {

    @Bean
    fun chain(steps: List<IndexingStep>): IndexingChain {
        return steps.asChain {
            then(LoadExistingProjectStep::class)
            then(ContinuationVerificationStep::class)
            if (isUnsubscriptionEnabled()) {
                then(SubscriptionVerificationStep::class)
            }
            then(ReadFeaturesStep::class)
            then(SaveProjectStep::class)
            then(ProjectIndexedEventPublisherStep::class)
        }
    }

    private fun isUnsubscriptionEnabled(): Boolean {
        return with(properties.subscription.unsubscribe) {
            filename != null || topic != null
        }
    }

    private fun List<IndexingStep>.asChain(withBuilder: IndexingChain.Builder.() -> Unit): IndexingChain {
        val builder = IndexingChain.Builder(this)
        withBuilder(builder)
        return builder.build()
    }
}