package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.indexing.project.IndexingChain
import com.roche.ambassador.indexing.project.IndexingContext
import com.roche.ambassador.storage.project.ProjectEntityRepository
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(0)
internal class LoadExistingProjectStep(private val projectEntityRepository: ProjectEntityRepository) : IndexingStep {
    override suspend fun handle(context: IndexingContext, chain: IndexingChain) {
        projectEntityRepository.findById(context.project.id).ifPresent {
            context.entity = it
        }
        if (context.entity?.lastIndexingId in context.continuation.unfinishedIndexingIds) {

        }
        chain.accept(context)
    }

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE
}
