package com.filipowm.ambassador.configuration

import com.filipowm.ambassador.configuration.concurrent.ConcurrencyProperties
import com.filipowm.ambassador.configuration.source.ProjectSourcesProperties
import com.filipowm.ambassador.events.EventsProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    ConcurrencyProperties::class,
    ProjectSourcesProperties::class,
    EventsProperties::class
)
open class ConfigurationPropertiesDiscovery
