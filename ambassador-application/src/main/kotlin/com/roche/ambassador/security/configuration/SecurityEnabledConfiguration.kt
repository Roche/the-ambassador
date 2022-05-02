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
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.DelegatingServerAuthenticationSuccessHandler
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.security.web.server.savedrequest.ServerRequestCache
import reactor.core.publisher.Mono

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@EnableConfigurationProperties(SessionProperties::class)
@ConditionalOnProperty(prefix = "ambassador.security", name = ["enabled"], havingValue = "true", matchIfMissing = true)
internal class SecurityEnabledConfiguration(
    configurers: List<SecurityConfigurer>,
    private val securityProperties: SecurityProperties,
    private val sessionProperties: SessionProperties,
    private val projectSourcesProperties: ProjectSourcesProperties,
    private val projectSources: ProjectSources
) : BaseSecurityConfiguration(configurers) {

    companion object {
        private val log by LoggerDelegate()
    }

    @Bean
    fun reactiveClientRegistrationRepository(): InMemoryReactiveClientRegistrationRepository {
        val registrar = ClientRegistrationRegistrar(projectSourcesProperties)
        return registrar.createRegistrations(listOf(*projectSources.getAll().toTypedArray()))
    }

    @Bean
    fun ambassadorUserDetailsService(repository: InMemoryReactiveClientRegistrationRepository): AmbassadorUserService {
        // TODO make it more friendly to create holder and create mapping of registration to source
        val holder = OAuth2ProvidersHolder()
        for (registration in repository) {
            projectSources.getByName(registration.clientName).ifPresent { holder.add(registration, it) }
        }
        if (holder.isEmpty()) {
            throw IllegalStateException("Unable to find any matching project source for registrations")
        }
        return AmbassadorUserService(holder)
    }

    override fun configure(http: ServerHttpSecurity) {
        log.info("Enabling web security")
        val requestCache = RedirectUriAwareCookieServerRequestCache(securityProperties.allowedRedirectUris)
        //@formatter:off
        http
            .requestCache().requestCache(requestCache).and()
            .authorizeExchange()
                .pathMatchers("/actuator/health/**").permitAll()
            .anyExchange().authenticated().and()
            .oauth2Login()
                .authenticationSuccessHandler(buildSuccessHandler(sessionProperties, requestCache))
                .authenticationFailureHandler(AmbassadorAuthenticationFailureHandler)
        //@formatter:on
    }

    private fun buildSuccessHandler(sessionProperties: SessionProperties, requestCache: ServerRequestCache): ServerAuthenticationSuccessHandler {
        val redirectSuccessHandler = RedirectServerAuthenticationSuccessHandler()
        redirectSuccessHandler.setRequestCache(requestCache)
        return DelegatingServerAuthenticationSuccessHandler(
            SessionConfiguringAuthenticationSuccessHandler(sessionProperties),
            LoggingAuthenticationSuccessHandler,
            redirectSuccessHandler // keep this one always, otherwise redirect from OAuth login would not work
        )
    }

    private object AmbassadorAuthenticationFailureHandler : RedirectServerAuthenticationFailureHandler("/login?error") {

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
