package com.roche.ambassador.configuration.health

import com.roche.ambassador.ConcurrencyProvider
import com.roche.ambassador.extensions.toCamelCase
import com.roche.ambassador.model.source.ProjectSources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor
import org.springframework.boot.actuate.health.ReactiveHealthContributor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SourcesHealthConfiguration {

    companion object {
        private const val GRACE_PERIOD_SECONDS = 15
    }

    @Bean("sourcesHealthIndicator")
    fun sourcesHealthIndicator(projectSources: ProjectSources, concurrencyProvider: ConcurrencyProvider): ReactiveHealthContributor {
        val scope = CoroutineScope(concurrencyProvider.getSupportingDispatcher() + SupervisorJob())
        val indicators = projectSources
            .getAll()
            .map { it.name().toCamelCase() to PingableHealthIndicator(it, GRACE_PERIOD_SECONDS, scope) }
            .toMap()
        return CompositeReactiveHealthContributor.fromMap(indicators)
    }
}
