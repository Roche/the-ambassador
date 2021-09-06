package com.filipowm.gitlab.api.client

import com.filipowm.gitlab.api.exceptions.Exceptions
import io.github.resilience4j.core.IntervalBiFunction
import io.github.resilience4j.core.IntervalFunction
import io.ktor.http.*
import io.vavr.control.Either
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

// see: https://docs.gitlab.com/ee/user/admin_area/settings/user_and_ip_rate_limits.html#response-headers
class RetryIntervalProvider(private val minimumInitialInterval: Duration, private val intervalFunctionSupplier: (Duration) -> IntervalFunction) : IntervalBiFunction<Any> {

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
        val waitTime = exception.headers.extractHeaderAsLong(HttpHeaders.RetryAfter)
            .or { exception.headers.extractHeaderAsLong("RateLimit-Reset").map { it - System.currentTimeMillis() } }
            .map { Duration.ofSeconds(it) }
            .orElse(minimumInitialInterval)
        log.warn("Rate limit reached, waiting for {}s", waitTime.seconds)
        return waitTime
    }


    private fun Headers.extractHeaderAsLong(name: String): Optional<Long> {
        return Optional.ofNullable(this[name])
            .flatMap { Optional.ofNullable(it.toLongOrNull()) }
    }
}
