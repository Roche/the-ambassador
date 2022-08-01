package com.roche.ambassador.analysis

import com.roche.ambassador.ConcurrencyProvider
import com.roche.ambassador.extensions.LoggerDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
internal class OnDemandAnalysisService(
    private val delegate: ProjectScoringAnalysisService,
    concurrencyProvider: ConcurrencyProvider
) : AnalysisService {

    companion object {
        private val log by LoggerDelegate()
    }

    private val analysisScope: CoroutineScope = CoroutineScope(concurrencyProvider.getSupportingDispatcher())

    override fun analyzeOne(id: String) {
        log.info("Running on-demand analysis of project '$id'")
        delegate.analyzeOne(id)
        log.info("On-demand analysis of project '$id' ended")
    }

    override fun analyzeAll() {
        log.info("Launching on-demand analysis of all projects")
        analysisScope.launch {
            delegate.analyzeAll()
        }
    }

}