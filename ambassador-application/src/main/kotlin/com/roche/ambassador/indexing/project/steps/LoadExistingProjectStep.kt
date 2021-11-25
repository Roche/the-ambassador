package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.indexing.project.IndexingContext
import com.roche.ambassador.storage.project.ProjectEntityRepository
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(3)
internal class LoadExistingProjectStep(private val projectEntityRepository: ProjectEntityRepository) : IndexingStep {
    override suspend fun handle(context: IndexingContext) {
        projectEntityRepository.findById(context.project.id).ifPresent {
            context.entity = it
        }
    }

}