package com.roche.ambassador.security.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.validation.annotation.Validated
import javax.validation.Valid

@ConfigurationProperties(prefix = "ambassador.security")
@ConstructorBinding
@Validated
data class SecurityProperties(
    val allowedRedirectUris: List<String> = listOf(),

    @NestedConfigurationProperty
    @Valid
    val cors: CorsProperties = CorsProperties(),

    val redirectOnUnauthenticated: Boolean = true,

    val loginUrl: String = "/api/v1/login/oauth2/{registrationId}",
    val logoutUrl: String = "/api/v1/logout",
) {

    data class CorsProperties(val allowedOrigins: List<String> = listOf("*"))

}