package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.model.Project

interface ProjectIndexer {

    suspend fun indexOne(id: Long): Project
    suspend fun indexAll(
        onStarted: IndexingStartedCallback = {},
        onFinished: IndexingFinishedCallback = {},
        onError: IndexingErrorCallback = {},
        onProjectIndexingStarted: ProjectIndexingStartedCallback = {},
        onProjectIndexingError: ProjectIndexingErrorCallback = { _: Throwable, _: Any -> },
        onProjectIndexingFinished: ProjectIndexingFinishedCallback = {}
    )

    fun forciblyStop()
}

typealias IndexingStartedCallback = () -> Unit
typealias IndexingErrorCallback = (Throwable) -> Unit
typealias IndexingFinishedCallback = () -> Unit
typealias ProjectIndexingFinishedCallback = (Project) -> Unit
typealias ProjectIndexingErrorCallback = (Throwable, Any) -> Unit
typealias ProjectIndexingStartedCallback = (Any) -> Unit