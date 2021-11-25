package com.roche.ambassador.indexing

import com.roche.ambassador.indexing.project.ProjectIndexer
import com.roche.ambassador.model.source.ProjectSource

interface IndexerFactory {

    fun create(source: ProjectSource): ProjectIndexer
}
