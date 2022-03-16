package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.project.IndexingChain
import com.roche.ambassador.indexing.project.IndexingContext
import com.roche.ambassador.model.FeatureReader
import com.roche.ambassador.model.feature.FeatureReaders
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.storage.project.ProjectEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.springframework.stereotype.Component

@Component
internal class ReadFeaturesStep : IndexingStep {

    companion object {
        private val log by LoggerDelegate()
    }

    override suspend fun handle(context: IndexingContext, chain: IndexingChain) {
        if (context.project.visibility !in context.config.features.requireVisibility) {
            log.info("Features will not be read for project '{}' (id={}), because it is disabled for project's visibility", context.project.fullName, context.project.id)
        } else if (context.subscribed) {
            readFeatures(context)
        }
        chain.accept(context)
    }

    private suspend fun readFeatures(context: IndexingContext) {
        val entity = context.entity
        val currentProject = context.project
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
    }

    private suspend fun readFeatures(readers: List<FeatureReader<*>>, context: IndexingContext) {
        readers.map {
            context.coroutineScope.async {
                context.project.readFeature(it, context.source)
            }
        }.awaitAll()
    }
}
