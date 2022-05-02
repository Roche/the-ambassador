package com.roche.ambassador.security.configuration

import com.roche.ambassador.extensions.LoggerDelegate
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Component
class CorsConfigurer(private val securityProperties: SecurityProperties) : SecurityConfigurer {

    companion object {
        private const val ALLOW_ALL: String = "*"
        private val log by LoggerDelegate()
    }

    override fun configure(http: ServerHttpSecurity) {
        http.cors().configure()
    }

    private fun ServerHttpSecurity.CorsSpec.configure(): ServerHttpSecurity {
        val corsConfig = UrlBasedCorsConfigurationSource()
        val _allowedOrigins = securityProperties.cors.allowedOrigins.ifEmpty {
            listOf(ALLOW_ALL)
        }
        log.info("Setting up CORS with allowed origins: {}", _allowedOrigins)
        val cors = with(CorsConfiguration()) {
            allowedOrigins = _allowedOrigins
            allowedMethods = listOf(ALLOW_ALL)
            allowedHeaders = listOf(ALLOW_ALL)
            allowCredentials = ALLOW_ALL !in securityProperties.cors.allowedOrigins && securityProperties.cors.allowedOrigins.isNotEmpty()
            this
        }
        corsConfig.registerCorsConfiguration("/**", cors)
        return configurationSource(corsConfig).and()
    }
}