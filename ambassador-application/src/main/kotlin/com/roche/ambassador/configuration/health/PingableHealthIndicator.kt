package com.roche.ambassador.configuration.health

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
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

internal class PingableHealthIndicator(
    private val pingable: Pingable,
    gracePeriodSeconds: Int,
    private val pingScope: CoroutineScope
) : ReactiveHealthIndicator {

    private val lastPingedAt: AtomicLong = AtomicLong(0)
    private val lastStatus: AtomicReference<Health> = AtomicReference(UP)
    private val gracePeriodMillis = gracePeriodSeconds * 1000

    companion object {
        private val log by LoggerDelegate()
        private val UP = Health.up().build()
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
                UP
            } else {
                lastStatus.get()
            }
        } catch (ex: UnhealthyComponentException) {
            log.trace("Component is unhealthy. Status: {}", ex.status, ex)
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
}
