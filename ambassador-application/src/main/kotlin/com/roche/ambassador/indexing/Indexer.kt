package com.roche.ambassador.indexing

interface Indexer<T, ID, F> {

    suspend fun indexOne(id: ID): T
    suspend fun indexAll(
        filter: F,
        onStarted: IndexingStartedCallback = {},
        onFinished: IndexingFinishedCallback = {},
        onError: IndexingErrorCallback = {},
        onObjectIndexingStarted: ObjectIndexingStartedCallback<T> = {},
        onObjectExcludedByCriteria: ObjectExcludedByCriteriaCallback<T> = { _, _ -> },
        onObjectIndexingError: ObjectIndexingErrorCallback<T> = { _, _ -> },
        onObjectIndexingFinished: ObjectIndexingFinishedCallback<T> = {}
    )

    fun forciblyStop(terminateImmediately: Boolean)
}

typealias IndexingStartedCallback = () -> Unit
typealias IndexingErrorCallback = (Throwable) -> Unit
typealias IndexingFinishedCallback = () -> Unit
typealias ObjectIndexingFinishedCallback<T> = (T) -> Unit
typealias ObjectIndexingErrorCallback<T> = (Throwable, T) -> Unit
typealias ObjectExcludedByCriteriaCallback<T> = (List<IndexingCriterion>, T) -> Unit
typealias ObjectIndexingStartedCallback<T> = (T) -> Unit
