package com.filipowm.ambassador.security

import org.springframework.boot.autoconfigure.session.SessionProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.DelegatingServerAuthenticationSuccessHandler
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@EnableConfigurationProperties(SessionProperties::class)
internal class SecurityConfiguration {

    @Bean
    fun springWebFilterChain(http: ServerHttpSecurity, sessionProperties: SessionProperties): SecurityWebFilterChain {
        return http
            .csrf().disable()
            .authorizeExchange()
            .anyExchange().authenticated().and()
            .httpBasic().disable()
            .oauth2Login()
            .authenticationSuccessHandler(buildSuccessHandler(sessionProperties))
            .and()
            .build()
    }

    private fun buildSuccessHandler(sessionProperties: SessionProperties): ServerAuthenticationSuccessHandler {
        return DelegatingServerAuthenticationSuccessHandler(
            SessionConfiguringAuthenticationSuccessHandler(sessionProperties),
            LoggingAuthenticationSuccessHandler,
            RedirectServerAuthenticationSuccessHandler() // keep this one always, otherwise redirect from OAuth login would not work
        )
    }
}
