package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.ConcurrencyProvider
import com.filipowm.ambassador.model.Project
import com.filipowm.ambassador.model.source.ProjectSource
import com.filipowm.ambassador.storage.ProjectEntityRepository
import java.time.Duration

class CoreProjectIndexer(
    private val source: ProjectSource<Any>,
    private val projectEntityRepository: ProjectEntityRepository,
    private val concurrencyProvider: ConcurrencyProvider,
    private val indexEvery: Duration
) : ProjectIndexer {
    override suspend fun indexOne(id: Long): Project {
        TODO("Not yet implemented")
    }

    override suspend fun indexAll() {
        TODO("Not yet implemented")
    }
}