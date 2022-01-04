package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.project.IndexingContext
import com.roche.ambassador.storage.project.ProjectEntity
import com.roche.ambassador.storage.project.ProjectEntityRepository
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Order(6)
internal class SaveProjectStep(private val projectEntityRepository: ProjectEntityRepository) : IndexingStep {

    companion object {
        private val log by LoggerDelegate()
    }

    @Transactional
    override suspend fun handle(context: IndexingContext) {
        val currentEntity = context.entity
        val toSave: ProjectEntity = if (currentEntity != null) {
            currentEntity.removeHistoryToMatchLimit(5) // indexerProperties.historySize - 1)
            currentEntity.snapshot()
            currentEntity.updateIndex(context.project)
            currentEntity
        } else {
            ProjectEntity.from(context.project)
        }
        val result = projectEntityRepository.save(toSave)
        log.info("Indexed project '{}' (id={})", context.project.name, context.project.id)
        context.entity = result
    }
}
