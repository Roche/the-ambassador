package pl.filipowm.opensource.ambassador

import kotlinx.coroutines.CoroutineDispatcher
import reactor.core.scheduler.Scheduler
import java.util.concurrent.ExecutorService

interface ConcurrencyProvider {

    fun getExecutor() : ExecutorService
    fun getCoroutineDispatcher() : CoroutineDispatcher

    fun getScheduler(): Scheduler
}