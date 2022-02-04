package com.roche.ambassador.configuration

import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.configuration.source.ProjectSourcesProperties
import com.roche.ambassador.configuration.web.OpenApiProperties
import com.roche.ambassador.events.EventsProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties(
    ProjectSourcesProperties::class,
    EventsProperties::class,
    IndexerProperties::class,
    OpenApiProperties::class
)
internal class ConfigurationPropertiesDiscovery
