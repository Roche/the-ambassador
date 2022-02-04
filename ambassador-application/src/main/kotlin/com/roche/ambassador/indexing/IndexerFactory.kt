package com.roche.ambassador.indexing

import com.roche.ambassador.indexing.project.ProjectIndexer
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.storage.indexing.Indexing

interface IndexerFactory {

    fun create(source: ProjectSource, indexing: Indexing, continuation: Continuation): ProjectIndexer
}
