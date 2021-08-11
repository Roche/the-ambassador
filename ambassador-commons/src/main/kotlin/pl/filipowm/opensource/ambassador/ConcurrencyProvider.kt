package pl.filipowm.opensource.ambassador

import kotlinx.coroutines.CoroutineDispatcher

interface ConcurrencyProvider {

    fun getSourceProjectProducerDispatcher(): CoroutineDispatcher
    fun getIndexingConsumerDispatcher(): CoroutineDispatcher

}