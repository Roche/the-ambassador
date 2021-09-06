package com.filipowm.ambassador.project.indexer

import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

internal class Statistics {

    private val started = AtomicLong()
    private val finished = AtomicLong()
    private val errors = ConcurrentHashMap<String, AtomicLong>()
    private val timer = Timer()

    fun recordStarted() = started.incrementAndGet()
    fun recordFinished() = finished.incrementAndGet()
    fun recordError(t: Throwable) = errors.computeIfAbsent(t.javaClass.simpleName) { AtomicLong() }.incrementAndGet()

    fun getProjectsStarted() = started.get()
    fun getProjectsIndexed() = finished.get()
    fun getDuration() = timer.getDuration()
    fun getTotalErrors(): Long {
        if (errors.isEmpty()) {
            return 0L
        }
        return errors.values
            .map { it.get() }
            .reduce { acc, value -> acc + value }
    }

    fun startTiming() = timer.start()
    fun stopTiming() = timer.stop()

    fun getReport(): String {
        val sb = StringBuilder()
        sb.appendLine("Total projects: ${getProjectsStarted()}")
        sb.appendLine("Indexed projects: ${getProjectsIndexed()}")
        sb.appendLine("Total errors: ${getTotalErrors()}")
        sb.appendLine("Total indexing time: ${getDuration().humanReadableFormat()}")
        sb.appendLine("Avg time per project: ${timer.getDurationAsMillis() / getProjectsIndexed()}ms")
        if (getTotalErrors() > 0) {
            sb.appendLine("Most occurring errors:")
            errors.forEach { (error, count) -> sb.appendLine("  - ${error}: ${count.get()}") }
        }

        return sb.toString()
    }
//    """
//        Total projects: ${getProjectsStarted()}
//        Total indexed projects: ${getProjectsIndexed()}
//        Total errors: ${getTotalErrors()}
//        Total indexing time: ${getDuration().humanReadableFormat()}
//        Most occurring errors:
//        ${getErrorsReport()}
//    """.trimIndent()

    private fun Duration.humanReadableFormat(): String {
        return toString()
            .substring(2)
            .replace("(\\d[HMS])(?!$)".toRegex(), "$1 ")
            .toLowerCase()
    }

    private class Timer(private val millisProvider: () -> Long = { System.currentTimeMillis() }) {
        private var start = 0L
        private var duration = 0L

        fun getDuration(): Duration = Duration.ofMillis(duration)
        fun getDurationAsMillis(): Long = duration

        fun start() {
            if (start > 0L) {
                throw IllegalStateException("Timer already started. Create new timer to take new timing")
            }
            start = millisProvider()
        }

        fun stop(): Long {
            if (start == 0L) {
                throw IllegalStateException("Timer not yet started. Start timer first to be able to stop it")
            }
            if (duration > 0L) {
                throw IllegalStateException("Timer already stopped. Create new timer to take new timing")
            }
            duration = millisProvider() - start
            return duration
        }
    }
}
