package com.roche.ambassador.configuration.health

import com.roche.ambassador.configuration.health.Constants.DOWN
import com.roche.ambassador.configuration.health.Constants.UP
import com.roche.ambassador.extensions.LoggerDelegate
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.ReactiveHealthIndicator
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicReference

@Component("startupState")
internal class StartupProbeIndicator : ReactiveHealthIndicator {

    companion object {
        private val log by LoggerDelegate()
    }

    private val status: AtomicReference<Health> = AtomicReference(DOWN)

    @EventListener
    fun handleContextStartedEvent(event: ContextRefreshedEvent) {
        if (status.compareAndSet(DOWN, UP)) {
            log.debug("Application started. Startup probe should report success.")
        }
    }

    override fun health(): Mono<Health> = Mono.just(status.get())

}