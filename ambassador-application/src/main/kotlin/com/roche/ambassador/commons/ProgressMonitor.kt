package com.roche.ambassador.commons

import com.roche.ambassador.extensions.LoggerDelegate
import java.util.concurrent.atomic.AtomicLong

sealed interface ProgressMonitor {

    fun success()
    fun failure()
}

class LoggingProgressMonitor(private val total: Long,
                                      private val resolution: Int = 5,
                                      private val messageProvider: ProgressMessageProvider = defaultProgressMessageProvider) : ProgressMonitor {

    private val success: AtomicLong = AtomicLong(0)
    private val failure: AtomicLong = AtomicLong(0)

    companion object {
        private val log by LoggerDelegate()
        private val defaultProgressMessageProvider: ProgressMessageProvider = { successes, failures, total ->
            val percentage = (successes + failures) * 100 / total
            "Progress: $percentage% (successes: $successes; failures: $failures; total: $total)"
        }
    }

    override fun success() {
        val s = success.incrementAndGet()
        val f = failure.get()
        tryLog(s, f)
    }

    override fun failure() {
        val f = failure.incrementAndGet()
        val s = success.get()
        tryLog(s, f)
    }

    private fun tryLog(success: Long, failure: Long) {
        val width = total * resolution / 100
        val progress = success + failure
        if (progress % width == 0L) {
            val msg = messageProvider(success, failure, total)
            log.info(msg)
        }
    }

}

typealias ProgressMessageProvider = (Long, Long, Long) -> (String)