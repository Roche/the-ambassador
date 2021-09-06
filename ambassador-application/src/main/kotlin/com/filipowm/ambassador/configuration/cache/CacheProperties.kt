package com.filipowm.ambassador.configuration.cache

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@Component
@ConfigurationProperties("ambassador.cache")
@Validated
class CacheProperties {

}
