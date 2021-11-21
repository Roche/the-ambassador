package com.roche.ambassador.project.indexer

import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.project.ProjectFilter
import com.roche.ambassador.model.source.ProjectSource

interface ProjectIndexer {

    suspend fun indexOne(id: Long): Project
    suspend fun indexAll(
        filter: ProjectFilter = ProjectFilter(null, null, null),
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
typealias ProjectIndexingErrorCallback = (Throwable, Project) -> Unit
typealias ProjectExcludedByCriteriaCallback = (List<IndexingCriterion>, Project) -> Unit
typealias ProjectIndexingStartedCallback = (Project) -> Unit
