package com.roche.ambassador.project.indexer.steps

import com.roche.ambassador.storage.project.ProjectEntityRepository
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(3)
class LoadExistingProjectStep(private val projectEntityRepository: ProjectEntityRepository) : IndexingStep {
    override suspend fun handle(context: IndexingContext) {
        projectEntityRepository.findById(context.project.id).ifPresent {
            context.entity = it
        }
    }

}