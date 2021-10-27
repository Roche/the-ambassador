package com.roche.ambassador.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.roche.ambassador.storage.config.JsonStorageConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration
internal open class JsonConfiguration {

    @Bean
    @ConditionalOnMissingBean(ObjectMapper::class)
    open fun objectMapper(): ObjectMapper {
        return JsonStorageConfiguration.OBJECT_MAPPER
    }

    @Bean
    open fun jacksonMessageConverter(objectMapper: ObjectMapper): MappingJackson2HttpMessageConverter {
        return MappingJackson2HttpMessageConverter(objectMapper)
    }
}
