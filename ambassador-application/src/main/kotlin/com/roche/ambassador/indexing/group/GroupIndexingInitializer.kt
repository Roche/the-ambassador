package com.roche.ambassador.indexing.group

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.indexing.IndexingFinishedEvent
import com.roche.ambassador.model.source.ProjectSources
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
            .ifPresentOrElse( { groupIndexerFactory.create(it).indexAll() }) {
                log.warn(
                    "Source with name {} was not found. Unable to index groups after {} indexing",
                    indexingFinishedEvent.data.target, indexingFinishedEvent.data.getId()
                )
            }
    }
}