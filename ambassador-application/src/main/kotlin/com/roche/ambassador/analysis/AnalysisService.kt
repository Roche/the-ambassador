package com.roche.ambassador.analysis

import com.roche.ambassador.ConcurrencyProvider
import com.roche.ambassador.commons.LoggingProgressMonitor
import com.roche.ambassador.commons.ProgressMonitor
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.ScorecardCalculator
import com.roche.ambassador.model.ScorecardConfiguration
import com.roche.ambassador.model.feature.FeatureReaders
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.source.ProjectSources
import com.roche.ambassador.storage.project.ProjectEntity
import com.roche.ambassador.storage.project.ProjectEntityRepository
import com.roche.ambassador.storage.project.ProjectStatisticsHistory
import com.roche.ambassador.storage.project.ProjectStatisticsHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate

@Service
internal class AnalysisService(
    private val scorecardConfiguration: ScorecardConfiguration,
    private val projectEntityRepository: ProjectEntityRepository,
    private val projectStatisticsHistoryRepository: ProjectStatisticsHistoryRepository,
    private val projectSources: ProjectSources,
    platformTransactionManager: PlatformTransactionManager,
    concurrencyProvider: ConcurrencyProvider
) {

    private val transactionTemplate: TransactionTemplate = TransactionTemplate(platformTransactionManager)
    private val analysisScope: CoroutineScope = CoroutineScope(concurrencyProvider.getSupportingDispatcher())
    private val calculator: ScorecardCalculator = ScorecardCalculator(scorecardConfiguration)

    companion object {
        private val log by LoggerDelegate()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun analyzeOne(id: String) {
        val entity = projectEntityRepository.findById(id.toLong())
            .orElseThrow { IllegalStateException("Project does not exist!") }
        if (!entity.subscribed) {
            log.warn("Trying to analyze unsubscribed project. Skipping...")
            return
        }
        val progressMonitor: ProgressMonitor = LoggingProgressMonitor(1, 1, AnalysisService::class)
        analyze(entity, progressMonitor)
    }

    // TODO run async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun analyzeAll() {
        val total = projectEntityRepository.countAllBySubscribed(true)
        val progressMonitor: ProgressMonitor = LoggingProgressMonitor(total, 2, AnalysisService::class)
        projectEntityRepository.streamAllForAnalysis().use { stream ->
            stream
                .filter { scorecardConfiguration.shouldCalculateScoring(it.project) }
                .forEach { analyze(it, progressMonitor) }
        }
    }

    private fun analyze(entity: ProjectEntity, progressMonitor: ProgressMonitor) {
        analysisScope.launch {
            try {
                entity.project = analyze(entity.project)
                entity.updateScore(entity.project)
                transactionTemplate.executeWithoutResult {
                    val savedEntity = projectEntityRepository.save(entity)
                    val historyEntry = ProjectStatisticsHistory.from(savedEntity)
                    val entryDate = historyEntry.date.toLocalDate().atStartOfDay()
                    projectStatisticsHistoryRepository.deleteByProjectIdAndDateBetween(historyEntry.projectId, entryDate, entryDate.plusDays(1))
                    projectStatisticsHistoryRepository.save(historyEntry)
                }
                progressMonitor.success()
            } catch (e: Throwable) {
                log.error("Failed to analyze project '{}' (id={}).", entity.project.fullName, entity.project.id, e)
                progressMonitor.failure()
            }
        }
    }

    private suspend fun analyze(project: Project): Project {
        log.debug("Calculating scoring for project '{}' (id={})", project.name, project.id)
        val source = projectSources.get("gitlab").orElseThrow()
        FeatureReaders.getProjectBasedReaders().forEach {
            project.readFeature(it, source)
        }
        val scorecard = calculator.calculateFor(project)
        project.scorecard = scorecard
        return project
    }
}
