package pl.filipowm.opensource.ambassador.configuration

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import pl.filipowm.opensource.ambassador.configuration.concurrent.ConcurrencyProperties
import pl.filipowm.opensource.ambassador.configuration.source.ProjectSourceProperties

@Configuration
@EnableConfigurationProperties(ConcurrencyProperties::class, ProjectSourceProperties::class)
open class ConfigurationPropertiesDiscovery {
}