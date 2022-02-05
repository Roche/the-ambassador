package com.roche.ambassador.analysis

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.IndexingFinishedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class PostIndexingAnalysisInitializer(private val analysisService: AnalysisService) {

    companion object {
        private val log by LoggerDelegate()
    }

    @EventListener
    fun handleIndexingFinished(event: IndexingFinishedEvent) {
        log.info("Initializing indexed projects analysis & scoring.")
        analysisService.analyzeAll()
        log.info("Indexed projects were analyzed.")
    }
}
