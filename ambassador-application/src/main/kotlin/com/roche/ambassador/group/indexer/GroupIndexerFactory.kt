package com.roche.ambassador.group.indexer

import com.roche.ambassador.ConcurrencyProvider
import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.storage.group.GroupEntityRepository
import com.roche.ambassador.storage.project.ProjectEntityRepository
import kotlinx.coroutines.CoroutineScope
import org.springframework.stereotype.Component

@Component
internal class GroupIndexerFactory(
    private val projectEntityRepository: ProjectEntityRepository,
    private val groupEntityRepository: GroupEntityRepository,
    private val indexerProperties: IndexerProperties,
    concurrencyProvider: ConcurrencyProvider
) {

    private val coroutineScope = CoroutineScope(concurrencyProvider.getSupportingDispatcher())

    fun create(source: ProjectSource): GroupIndexer = GroupIndexer(source, projectEntityRepository, groupEntityRepository, coroutineScope, indexerProperties)
}