package com.filipowm.ambassador.configuration.web

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.tags.Tag
import org.springdoc.core.customizers.OpenApiCustomiser
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class OpenApiDocsConfiguration {

    @Bean
    fun openApi(propertiesAwareCustomizer: OpenApiCustomiser): OpenAPI {
        val openApi = OpenAPI()
        propertiesAwareCustomizer.customise(openApi)
        return openApi
    }

    @Bean
    fun propertiesAwareCustomizer(
        openApiProperties: OpenApiProperties,
        buildProperties: BuildProperties
    ): OpenApiCustomiser = OpenApiCustomiser {
        it.info = Info()
            .title(openApiProperties.title)
            .description(openApiProperties.description)
            .version(buildProperties.version)
        if (openApiProperties.contact != null) {
            val contact = Contact()
                .name(openApiProperties.contact!!.name)
                .email(openApiProperties.contact!!.email)
                .url(openApiProperties.contact!!.url)
            it.info.contact = contact
        }
        if (openApiProperties.license != null) {
            val license = License()
                .name(openApiProperties.license!!.name)
                .url(openApiProperties.license!!.url)
            it.info.license = license
        }
        if (openApiProperties.externalDocs != null) {
            it.externalDocs = ExternalDocumentation()
                .description(openApiProperties.externalDocs!!.description)
                .url(openApiProperties.externalDocs!!.url)
        }
        it.tags = openApiProperties.tags
            .map { (name, description) -> Tag().name(name).description(description) }
        it.info.addExtension("build time", buildProperties.time)
    }

}