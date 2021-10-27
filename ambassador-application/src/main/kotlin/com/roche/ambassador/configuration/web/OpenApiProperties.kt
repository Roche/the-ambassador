package com.roche.ambassador.configuration.web

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@ConfigurationProperties(prefix = "ambassador.openapi")
@ConstructorBinding
@Validated
internal data class OpenApiProperties(
    @NotBlank val title: String,
    val description: String?,
    val tags: List<Tag> = listOf(),
    @NestedConfigurationProperty val contact: Contact?,
    @NestedConfigurationProperty val license: License?,
    @NestedConfigurationProperty val externalDocs: ExternalDocs?,
) {

    data class Contact(val name: String?, val email: String?, val url: String?)

    data class License(val name: String?, val url: String?)

    data class ExternalDocs(val url: String?, val description: String?)

    data class Tag(val name: String?, val description: String?)
}