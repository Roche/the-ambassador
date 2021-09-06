package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.model.Project
import com.filipowm.ambassador.model.source.ProjectSource

interface ProjectIndexer {

    suspend fun indexOne(id: Long): Project
    suspend fun indexAll(
        onStarted: IndexingStartedCallback = {},
        onFinished: IndexingFinishedCallback = {},
        onError: IndexingErrorCallback = {},
        onProjectIndexingStarted: ProjectIndexingStartedCallback = {},
        onProjectExcludedByCriteria: ProjectExcludedByCriteriaCallback = { _, _ -> },
        onProjectIndexingError: ProjectIndexingErrorCallback = { _, _ -> },
        onProjectIndexingFinished: ProjectIndexingFinishedCallback = {}
    )

    fun forciblyStop(terminateImmediately: Boolean)
    fun getSource(): ProjectSource<Any>
}

typealias IndexingStartedCallback = () -> Unit
typealias IndexingErrorCallback = (Throwable) -> Unit
typealias IndexingFinishedCallback = () -> Unit
typealias ProjectIndexingFinishedCallback = (Project) -> Unit
typealias ProjectIndexingErrorCallback = (Throwable, Any) -> Unit
typealias ProjectExcludedByCriteriaCallback = (List<IndexingCriterion<Any>>, Any) -> Unit
typealias ProjectIndexingStartedCallback = (Any) -> Unit