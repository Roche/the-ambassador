package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.model.source.ProjectSource

interface IndexerFactory {

    fun create(source: ProjectSource<Any>): ProjectIndexer

}