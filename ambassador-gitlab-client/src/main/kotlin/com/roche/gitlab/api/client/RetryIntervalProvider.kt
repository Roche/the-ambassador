package com.roche.gitlab.api.client

import com.roche.gitlab.api.exceptions.Exceptions
import io.github.resilience4j.core.IntervalBiFunction
import io.github.resilience4j.core.IntervalFunction
import io.ktor.http.*
import io.vavr.control.Either
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.util.*

class RetryLogger(private val delegate: IntervalBiFunction<Any>) : IntervalBiFunction<Any> {

    companion object {
        private val log = LoggerFactory.getLogger(RetryIntervalProvider::class.java)
    }

    @Volatile
    private var lastRetryFinishTime: Instant = Instant.EPOCH

    override fun apply(attempts: Int, either: Either<Throwable, Any>): Long {
        val waitTime = delegate.apply(attempts, either)
        logRetryWaitTimeIfApplicable(waitTime)
        return waitTime
    }

    @Synchronized
    private fun logRetryWaitTimeIfApplicable(waitTime: Long) {
        val retryTriggeredTime = Instant.now()
        if (retryTriggeredTime.isAfter(lastRetryFinishTime)) {
            lastRetryFinishTime = retryTriggeredTime.plusMillis(waitTime)
            log.warn("Rate limit reached, waiting for {}s", waitTime / 1000)
        }
    }
}

// see: https://docs.gitlab.com/ee/user/admin_area/settings/user_and_ip_rate_limits.html#response-headers
class RetryIntervalProvider(
    private val minimumInitialInterval: Duration,
    private val intervalFunctionSupplier: (Duration) -> IntervalFunction
) : IntervalBiFunction<Any> {

    companion object {
        private val log = LoggerFactory.getLogger(RetryIntervalProvider::class.java)
    }

    override fun apply(attempts: Int, either: Either<Throwable, Any>): Long {
        var initialInterval = minimumInitialInterval
        if (either.isLeft) {
            val throwable = either.left!!
            initialInterval = when (throwable) {
                is Exceptions.RateLimitReachedException -> calculateRateLimitInitialInterval(throwable)
                else -> minimumInitialInterval
            }
        }
        return intervalFunctionSupplier(initialInterval).apply(attempts)
    }

    private fun calculateRateLimitInitialInterval(exception: Exceptions.RateLimitReachedException): Duration {
        return exception.headers.extractHeaderAsLong(HttpHeaders.RetryAfter)
            .or { exception.headers.extractHeaderAsLong("RateLimit-Reset").map { it - System.currentTimeMillis() } }
            .map { Duration.ofSeconds(it) }
            .orElse(minimumInitialInterval)
    }

    private fun Headers.extractHeaderAsLong(name: String): Optional<Long> {
        return Optional.ofNullable(this[name])
            .flatMap { Optional.ofNullable(it.toLongOrNull()) }
    }
}
