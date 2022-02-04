@file:Suppress("SpringJavaInjectionPointsAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection",
               "SpringJavaInjectionPointsAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection",
               "SpringJavaInjectionPointsAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection",
               "SpringJavaInjectionPointsAutowiringInspection"
)

package com.roche.ambassador.configuration.concurrent

import com.roche.ambassador.ConcurrencyProvider
import com.roche.ambassador.configuration.properties.IndexerProperties
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.slf4j.MDC
import org.springframework.scheduling.concurrent.CustomizableThreadFactory
import org.springframework.stereotype.Component
import java.util.concurrent.*
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

@Component
internal class ConcurrencyProviderImpl(indexerProperties: IndexerProperties) : ConcurrencyProvider {

    private val producerExecutor: ExecutorService
    private val consumerExecutor: ExecutorService
    private val supportingExecutor: ExecutorService

    init {
        val properties = indexerProperties.concurrency
        val consumerThreadFactory = CustomizableThreadFactory(properties.consumerThreadPrefix)
        val producerThreadFactory = CustomizableThreadFactory(properties.producerThreadPrefix)
        val supportingThreadFactory = CustomizableThreadFactory(properties.supportingThreadPrefix)
        val supportingExecutorThreads = max(1, ceil(properties.concurrencyLevel * 0.15).roundToInt())
        val producerExecutorThreads = max(1, ceil(properties.concurrencyLevel * 0.1).roundToInt())
        val consumerExecutorThreads = max(1, properties.concurrencyLevel - producerExecutorThreads - supportingExecutorThreads)
        producerExecutor = MdcThreadPoolExecutor.newWithInheritedMdcFixedThreadPool(producerExecutorThreads, producerThreadFactory)
        consumerExecutor = MdcThreadPoolExecutor.newWithInheritedMdcFixedThreadPool(consumerExecutorThreads, consumerThreadFactory)
        supportingExecutor = MdcThreadPoolExecutor.newWithInheritedMdcFixedThreadPool(supportingExecutorThreads, supportingThreadFactory)
    }

    override fun getSourceProjectProducerDispatcher(): CoroutineDispatcher = producerExecutor.asCoroutineDispatcher()

    override fun getIndexingConsumerDispatcher(): CoroutineDispatcher = consumerExecutor.asCoroutineDispatcher()

    override fun getSupportingDispatcher(): CoroutineDispatcher = supportingExecutor.asCoroutineDispatcher()

    class MdcThreadPoolExecutor private constructor(
        private val fixedContext: Map<String, String>?,
        corePoolSize: Int,
        maximumPoolSize: Int,
        keepAliveTime: Long,
        unit: TimeUnit,
        workQueue: BlockingQueue<Runnable>,
        threadFactory: ThreadFactory
    ) : ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory) {

        private val useFixedContext: Boolean = fixedContext != null
        private val contextForTask: Map<String, String>?
            get() = if (useFixedContext) fixedContext else MDC.getCopyOfContextMap()

        /**
         * All executions will have MDC injected. `ThreadPoolExecutor`'s submission methods (`submit()` etc.)
         * all delegate to this.
         */
        override fun execute(command: Runnable) {
            super.execute(wrap(command, contextForTask))
        }

        companion object {
            /**
             * Pool where task threads take MDC from the submitting thread.
             */
            fun newWithInheritedMdcFixedThreadPool(
                threads: Int,
                threadFactory: ThreadFactory
            ): MdcThreadPoolExecutor {
                return MdcThreadPoolExecutor(null, threads, threads, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue(), threadFactory)
            }

            fun wrap(runnable: Runnable, context: Map<String, String>?): Runnable {
                return Runnable {
                    val previous = MDC.getCopyOfContextMap()
                    if (context == null) {
                        MDC.clear()
                    } else {
                        MDC.setContextMap(context)
                    }
                    try {
                        runnable.run()
                    } finally {
                        if (previous == null) {
                            MDC.clear()
                        } else {
                            MDC.setContextMap(previous)
                        }
                    }
                }
            }
        }
    }
}
