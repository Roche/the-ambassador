package pl.filipowm.opensource.ambassador.configuration.concurrent

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.slf4j.MDC
import org.springframework.scheduling.concurrent.CustomizableThreadFactory
import org.springframework.stereotype.Component
import pl.filipowm.opensource.ambassador.ConcurrencyProvider
import java.util.concurrent.*
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

@Component
class ConcurrencyProviderImpl(properties: ConcurrencyProperties) : ConcurrencyProvider {

    private val producerExecutor: ExecutorService
    private val consumerExecutor: ExecutorService

    init {
        val consumerThreadFactory = CustomizableThreadFactory(properties.consumerThreadPrefix)
        val producerThreadFactory = CustomizableThreadFactory(properties.producerThreadPrefix)
        val producerExecutorThreads = max(1, ceil(properties.concurrencyLevel * 0.1).roundToInt())
        val consumerExecutorThreads = max(1, properties.concurrencyLevel - producerExecutorThreads)
        producerExecutor = MdcThreadPoolExecutor.newWithInheritedMdcFixedThreadPool(producerExecutorThreads, producerThreadFactory)
        consumerExecutor = MdcThreadPoolExecutor.newWithInheritedMdcFixedThreadPool(consumerExecutorThreads, consumerThreadFactory)
    }

    override fun getSourceProjectProducerDispatcher(): CoroutineDispatcher {
        return producerExecutor.asCoroutineDispatcher()
    }

    override fun getIndexingConsumerDispatcher(): CoroutineDispatcher {
        return consumerExecutor.asCoroutineDispatcher()
    }

    class MdcThreadPoolExecutor private constructor(
        private val fixedContext: Map<String, String>?, corePoolSize: Int, maximumPoolSize: Int,
        keepAliveTime: Long, unit: TimeUnit, workQueue: BlockingQueue<Runnable>, threadFactory: ThreadFactory
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
                threads: Int, threadFactory: ThreadFactory
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