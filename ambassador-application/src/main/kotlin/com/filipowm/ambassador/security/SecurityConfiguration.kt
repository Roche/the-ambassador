package com.filipowm.ambassador.security

import com.filipowm.ambassador.configuration.source.ProjectSources
import com.filipowm.ambassador.configuration.source.ProjectSourcesProperties
import com.filipowm.ambassador.extensions.LoggerDelegate
import org.springframework.boot.autoconfigure.session.SessionProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.*
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@EnableConfigurationProperties(SessionProperties::class)
internal class SecurityConfiguration {

    private val log by LoggerDelegate()

    @Bean
    fun springWebFilterChain(http: ServerHttpSecurity,
                             sessionProperties: SessionProperties,
                             projectSourcesProperties: ProjectSourcesProperties,
                             projectSources: ProjectSources
    ): SecurityWebFilterChain {
        log.info("Enabling web security...")
        val registrar = ClientRegistrationRegistrar(projectSourcesProperties)

        val registrationRepository = registrar.createRegistrations(listOf(*projectSources.getAll().toTypedArray()))
        return http
            .csrf().disable()
            .authorizeExchange()
                .anyExchange().authenticated().and()
            .httpBasic().disable()
            .oauth2Login()
                .authenticationSuccessHandler(buildSuccessHandler(sessionProperties))
                .authenticationFailureHandler(AmbassadorAuthenticationFailureHandler())
                .clientRegistrationRepository(registrationRepository)
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

    private class AmbassadorAuthenticationFailureHandler : RedirectServerAuthenticationFailureHandler("/login?error") {

        private val log by LoggerDelegate()

        override fun onAuthenticationFailure(webFilterExchange: WebFilterExchange, exception: AuthenticationException): Mono<Void> {
            val message : String = when(exception) {
                is OAuth2AuthenticationException -> "OAuth2 authentication failure caused by: ${exception.error?.description ?: exception.message}"
                else -> "Authentication failed due to: ${exception.message ?: "unknown reason"}"
            }
            log.warn(message)
            return super.onAuthenticationFailure(webFilterExchange, exception)
        }

    }
}
