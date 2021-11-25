package com.roche.ambassador

import kotlinx.coroutines.CoroutineDispatcher

interface ConcurrencyProvider {

    fun getSourceProjectProducerDispatcher(): CoroutineDispatcher
    fun getIndexingConsumerDispatcher(): CoroutineDispatcher
    fun getSupportingDispatcher(): CoroutineDispatcher

}
