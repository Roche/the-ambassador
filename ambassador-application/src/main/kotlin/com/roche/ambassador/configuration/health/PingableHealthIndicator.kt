package com.roche.ambassador.configuration.health

import com.roche.ambassador.configuration.health.Constants.UP
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.health.Pingable
import com.roche.ambassador.health.UnhealthyComponentException
import com.roche.ambassador.health.UnhealthyComponentException.Status.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.reactor.asMono
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.ReactiveHealthIndicator
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.ceil

internal class PingableHealthIndicator(
    private val pingable: Pingable,
    gracePeriodSeconds: Int,
    private val pingScope: CoroutineScope
) : ReactiveHealthIndicator {

    private val lastPingedAt: AtomicLong = AtomicLong(0)
    private val lastStatus: AtomicReference<Health> = AtomicReference(UP)
    private val gracePeriodMillis = gracePeriodSeconds * 1000
    private val tracer: PingTracer = PingTracer()

    companion object {
        private val log by LoggerDelegate()
    }

    override fun health(): Mono<Health> {
        return pingScope.async { doPing() }.asMono(Dispatchers.Default)
    }

    private suspend fun doPing(): Health {
        val health = try {
            val lastPinged = lastPingedAt.get()
            val currentTime = System.currentTimeMillis()
            if (currentTime - gracePeriodMillis > lastPinged) {
                lastPingedAt.set(currentTime)
                pingable.ping()
                tracer.healthy()
                UP
            } else {
                lastStatus.get()
            }
        } catch (ex: UnhealthyComponentException) {
            tracer.unhealthy(ex)
            when (ex.status) {
                UNAVAILABLE -> Health.outOfService().withException(ex)
                UNKNOWN -> Health.unknown().withException(ex)
                UNAUTHORIZED -> Health.outOfService().withDetail("cause", "Lack of valid authentication")
                RATE_LIMITED -> Health.outOfService().withDetail("cause", "Rate limited")
                else -> Health.down().withException(ex).down()
            }.build()
        }
        lastStatus.set(health)
        return health
    }

    /**
     * Log failures every 1 minute and restore to healthy
     */
    private inner class PingTracer {

        private val lastWasHealthy = AtomicBoolean(false)
        private val unhealthyCount = AtomicLong(0)
        private val logAttemptsEachTimes: Int = with(60_000 / gracePeriodMillis.toDouble()) {
            if (this > 1) {
                ceil(this).toInt()
            } else {
                1
            }
        }

        fun healthy() {
            if (!lastWasHealthy.compareAndExchange(false, true)) {
                val previousFailuresCount = unhealthyCount.getAndSet(0)
                log.info("Component '{}' became healthy again after {} failures", pingable.name(), previousFailuresCount)
            } else {
                log.trace("Component '{}' is healthy", pingable.name())
            }
        }

        fun unhealthy(ex: UnhealthyComponentException) {
            val count = unhealthyCount.incrementAndGet()
            if (count == 1L) {
                log.error("Component '{}' became unhealthy. Status: {}", pingable.name(), ex.status, ex)
            } else if (count % logAttemptsEachTimes == 0L)
                log.warn("Component '{}' is unhealthy. Status: {}", pingable.name(), ex.status, ex)
        }
    }
}
