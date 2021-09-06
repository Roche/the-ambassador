package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.model.Project

interface ProjectIndexer {

    suspend fun indexOne(id: Long): Project
    suspend fun indexAll()
}

//typealias IndexingStartedCallback = () -> Unit
//typealias IndexingErrorCallback = (Throwable) -> Unit
//typealias IndexingFinishedCallback = () -> Unit
//typealias ProjectIndexingFinishedCallback = (Throwable) -> Unit
//typealias ProjectIndexingErrorCallback = (Throwable) -> Unit
//typealias ProjectIndexingStartedCallback = (Throwable) -> Unit