package com.roche.ambassador.indexing.group

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.IndexingFinishedEvent
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.model.source.ProjectSources
import com.roche.ambassador.storage.indexing.Indexing
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class GroupIndexingInitializer(
    private val projectSources: ProjectSources,
    private val groupIndexerFactory: GroupIndexerFactory
) {

    companion object {
        private val log by LoggerDelegate()
    }

    // no locking here is needed, as it will not be invoked often an clash as of now is unlikely
    @EventListener
    fun handle(indexingFinishedEvent: IndexingFinishedEvent) {
        log.info("Indexing groups after indexing {} on {} has finished", indexingFinishedEvent.data.getId(), indexingFinishedEvent.data.source)
        projectSources.get(indexingFinishedEvent.data.source)
            .ifPresentOrElse( { triggerIndexing(indexingFinishedEvent.data, it) } ) {
                log.warn(
                    "Source with name {} was not found. Unable to index groups after {} indexing",
                    indexingFinishedEvent.data.target, indexingFinishedEvent.data.getId()
                )
            }
    }

    private fun triggerIndexing(indexing: Indexing, source: ProjectSource) {
        if (indexing.getId() == null) {
            throw IllegalStateException("Indexing ID should not be null")
        }
        groupIndexerFactory.create(source).indexByIndexingId(indexing.getId()!!)
    }
}
