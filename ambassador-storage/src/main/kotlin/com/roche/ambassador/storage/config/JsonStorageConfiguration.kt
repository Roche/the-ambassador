package com.roche.ambassador.storage.config

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.vladmihalcea.hibernate.type.util.ObjectMapperSupplier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JsonStorageConfiguration : ObjectMapperSupplier {

    companion object {
        val OBJECT_MAPPER: ObjectMapper = ObjectMapper()
            .registerModules(KotlinModule.Builder().build(), Jdk8Module(), JavaTimeModule(), AmbassadorModule())
            .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PUBLIC_ONLY)
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .enable(MapperFeature.AUTO_DETECT_FIELDS)
            .enable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
    }

    @Bean
    @ConditionalOnMissingBean
    fun objectMapper(): ObjectMapper = OBJECT_MAPPER

    override fun get(): ObjectMapper = OBJECT_MAPPER
}
