package pl.filipowm.opensource.ambassador.configuration.concurrent

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.slf4j.MDC
import org.springframework.scheduling.concurrent.CustomizableThreadFactory
import org.springframework.stereotype.Component
import pl.filipowm.opensource.ambassador.ConcurrencyProvider
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.*

@Component
class ConcurrencyProviderImpl(properties: ConcurrencyProperties) : ConcurrencyProvider {

    private val executor: ExecutorService

    init {
        val threadFactory = CustomizableThreadFactory(properties.threadPrefix)
        executor = MdcThreadPoolExecutor.newWithInheritedMdcFixedThreadPool(properties.concurrencyLevel, threadFactory)
    }

    override fun getExecutor(): ExecutorService {
        return executor
    }

    override fun getCoroutineDispatcher(): CoroutineDispatcher {
        return executor.asCoroutineDispatcher()
    }

    override fun getScheduler(): Scheduler {
        return Schedulers.fromExecutor(executor)
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