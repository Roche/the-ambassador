package com.filipowm.ambassador.configuration

import com.filipowm.ambassador.configuration.properties.IndexerProperties
import com.filipowm.ambassador.configuration.source.ProjectSourcesProperties
import com.filipowm.ambassador.configuration.web.OpenApiProperties
import com.filipowm.ambassador.events.EventsProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    ProjectSourcesProperties::class,
    EventsProperties::class,
    IndexerProperties::class,
    OpenApiProperties::class
)
internal open class ConfigurationPropertiesDiscovery
