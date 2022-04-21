package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.project.IndexingChain
import com.roche.ambassador.indexing.project.IndexingContext
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class GracePeriodVerificationStep : IndexingStep {

    companion object {
        private val log by LoggerDelegate()
    }

    override suspend fun handle(context: IndexingContext, chain: IndexingChain) {
        with(context) {
            val shouldBeIndexed = Optional.ofNullable(entity)
                .filter { !it.wasIndexedBefore(LocalDateTime.now().minus(config.gracePeriod)) }
                .isEmpty
            if (shouldBeIndexed) {
                chain.accept(this)
            } else {
                log.info("Project '{}' (id={}) was indexed recently and is within grace period. Skipping...", projectName, projectId)
            }
        }
    }
}
