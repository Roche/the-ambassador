package com.roche.ambassador.project.indexer

import com.roche.ambassador.model.source.ProjectSource

interface IndexerFactory {

    fun create(source: ProjectSource<Any>): ProjectIndexer

}