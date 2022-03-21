package com.roche.ambassador.security.configuration

import com.roche.ambassador.configuration.source.ProjectSourcesProperties
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.source.ProjectSources
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.session.SessionProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.DelegatingServerAuthenticationSuccessHandler
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@EnableConfigurationProperties(SessionProperties::class)
@ConditionalOnProperty(prefix = "ambassador.security", name = ["enabled"], havingValue = "true", matchIfMissing = true)
internal class SecurityConfiguration {

    private val log by LoggerDelegate()

    @Bean
    fun reactiveClientRegistrationRepository(
        projectSourcesProperties: ProjectSourcesProperties,
        projectSources: ProjectSources
    ): InMemoryReactiveClientRegistrationRepository {
        val registrar = ClientRegistrationRegistrar(projectSourcesProperties)
        return registrar.createRegistrations(listOf(*projectSources.getAll().toTypedArray()))
    }

    @Bean
    fun ambassadorUserDetailsService(
        repository: InMemoryReactiveClientRegistrationRepository,
        projectSources: ProjectSources
    ): AmbassadorUserService {
        // TODO make it more friendly to create holder and create mapping of registration to source
        val holder = OAuth2ProvidersHolder()
        for (registration in repository) {
            projectSources.getByName(registration.clientName)
                .ifPresent { holder.add(registration, it) }
        }
        if (holder.isEmpty()) {
            throw IllegalStateException("Unable to find any matching project source for registrations")
        }
        return AmbassadorUserService(holder)
    }

    @Bean
    fun springWebFilterChain(
        http: ServerHttpSecurity,
        sessionProperties: SessionProperties,
        reactiveClientRegistrationRepository: ReactiveClientRegistrationRepository
    ): SecurityWebFilterChain {
        log.info("Enabling web security...")
        // @formatter:off
        return http
            .csrf().disable()
            .cors().configure()
            .httpBasic().disable()
            .formLogin().disable()
            .authorizeExchange()
            .pathMatchers("/actuator/health/**").permitAll()
            .anyExchange().authenticated().and()
            .oauth2Login()
            .authenticationSuccessHandler(buildSuccessHandler(sessionProperties))
            .authenticationFailureHandler(AmbassadorAuthenticationFailureHandler())
            .clientRegistrationRepository(reactiveClientRegistrationRepository)
            .and()
            .build()
        // @formatter:on
    }

    private fun buildSuccessHandler(sessionProperties: SessionProperties): ServerAuthenticationSuccessHandler {
        return DelegatingServerAuthenticationSuccessHandler(
            SessionConfiguringAuthenticationSuccessHandler(sessionProperties),
            LoggingAuthenticationSuccessHandler,
            RedirectServerAuthenticationSuccessHandler() // keep this one always, otherwise redirect from OAuth login would not work
        )
    }

    private fun ServerHttpSecurity.CorsSpec.configure(): ServerHttpSecurity {
        val corsConfig = UrlBasedCorsConfigurationSource()
        val cors = with(CorsConfiguration()) {
            allowedOrigins = listOf("*")
            allowedMethods = listOf("*")
            allowedHeaders = listOf("*")
            allowCredentials = true
            this
        }
        corsConfig.registerCorsConfiguration("/**", cors)
        return configurationSource(corsConfig).and()
    }

    private class AmbassadorAuthenticationFailureHandler : RedirectServerAuthenticationFailureHandler("/login?error") {

        private val log by LoggerDelegate()

        override fun onAuthenticationFailure(webFilterExchange: WebFilterExchange, exception: AuthenticationException): Mono<Void> {
            val message: String = when (exception) {
                is OAuth2AuthenticationException -> "OAuth2 authentication failure caused by: ${exception.error?.description ?: exception.message}"
                else -> "Authentication failed due to: ${exception.message ?: "unknown reason"}"
            }
            log.warn(message, exception)
            return super.onAuthenticationFailure(webFilterExchange, exception)
        }
    }
}
