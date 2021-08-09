package pl.filipowm.opensource.ambassador.configuration

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import pl.filipowm.opensource.ambassador.configuration.concurrent.ConcurrencyProperties

@Configuration
@EnableConfigurationProperties(ConcurrencyProperties::class)
open class ConfigurationPropertiesDiscovery {
}