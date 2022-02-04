package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.project.IndexingChain
import com.roche.ambassador.indexing.project.IndexingContext
import com.roche.ambassador.storage.project.ProjectEntity
import com.roche.ambassador.storage.project.ProjectEntityRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
internal class SaveProjectStep(private val projectEntityRepository: ProjectEntityRepository) : IndexingStep {

    companion object {
        private val log by LoggerDelegate()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override suspend fun handle(context: IndexingContext, chain: IndexingChain) {
        val currentEntity = context.entity
        val toSave: ProjectEntity = if (currentEntity != null) {
            currentEntity.removeHistoryToMatchLimit(5) // )
            currentEntity.snapshot()
            currentEntity.updateIndex(context.project)
            currentEntity
        } else {
            ProjectEntity.from(context.project)
        }
        toSave.lastIndexingId = context.indexing.getId()
        val result = projectEntityRepository.save(toSave)
        log.info("Indexed project '{}' (id={})", context.project.fullName, context.project.id)
        context.entity = result
        chain.accept(context)
    }

    override fun getOrder(): Int = 6
}
