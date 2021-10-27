package com.roche.ambassador.events

import com.roche.ambassador.configuration.properties.AsyncProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "ambassador.events")
@Validated
@ConstructorBinding
internal class EventsProperties(
    @NestedConfigurationProperty
    val async: AsyncProperties = AsyncProperties()
)