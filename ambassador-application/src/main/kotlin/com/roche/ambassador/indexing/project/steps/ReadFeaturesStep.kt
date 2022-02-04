package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.project.IndexingChain
import com.roche.ambassador.indexing.project.IndexingContext
import com.roche.ambassador.model.FeatureReader
import com.roche.ambassador.model.feature.FeatureReaders
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.springframework.stereotype.Component

@Component
internal class ReadFeaturesStep : IndexingStep {

    companion object {
        private val log by LoggerDelegate()
    }

    override suspend fun handle(context: IndexingContext, chain: IndexingChain) {
        val entity = context.entity
        val currentProject = context.project
        if (context.project.visibility !in context.config.features.requireVisibility) {
            log.info("Features will not be read for project '{}' (id={}), because it is disabled for project's visibility", context.project.fullName, context.project.id)
            return
        }
        if (entity != null && !entity.wasIndexedBefore(currentProject.lastActivityDate!!)) {
            log.info(
                "Project '{}' (id={}) did not change since last indexing, re-using existing features",
                context.project.fullName, context.project.id
            )
            context.project.features.addAll(entity.project.features)
            readFeatures(FeatureReaders.getProjectBasedReaders(), context)
        } else {
            log.debug("Reading features for project '{}' (id={})", context.project.fullName, context.project.id)
            readFeatures(FeatureReaders.all(), context)
        }
        chain.accept(context)
    }

    override fun getOrder(): Int = 1

    private suspend fun readFeatures(readers: List<FeatureReader<*>>, context: IndexingContext) {
        readers.map {
            context.coroutineScope.async {
                context.project.readFeature(it, context.source)
            }
        }.awaitAll()
    }
}
