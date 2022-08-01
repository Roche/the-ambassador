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
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.DelegatingServerAuthenticationSuccessHandler
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.security.web.server.savedrequest.ServerRequestCache
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import reactor.core.publisher.Mono

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@EnableConfigurationProperties(SessionProperties::class)
@ConditionalOnProperty(prefix = "ambassador.security", name = ["enabled"], havingValue = "true", matchIfMissing = true)
internal class SecurityEnabledConfiguration(
    configurers: List<SecurityConfigurer>, private val securityProperties: SecurityProperties, private val sessionProperties: SessionProperties,
    projectSourcesProperties: ProjectSourcesProperties, private val projectSources: ProjectSources
) : BaseSecurityConfiguration(configurers) {

    companion object {
        private val log by LoggerDelegate()
    }

    private val oauthRegistrationRepository = ClientRegistrationRegistrar(projectSourcesProperties)
        .createRegistrations(listOf(*projectSources.getAll().toTypedArray()))

    @Bean
    fun ambassadorUserDetailsService(): AmbassadorUserService {
        // TODO make it more friendly to create holder and create mapping of registration to source
        val holder = OAuth2ProvidersHolder()
        for (registration in oauthRegistrationRepository) {
            projectSources.getByName(registration.clientName).ifPresent { holder.add(registration, it) }
        }
        if (holder.isEmpty()) {
            throw IllegalStateException("Unable to find any matching project source for registrations")
        }
        return AmbassadorUserService(holder)
    }

    override fun configure(http: ServerHttpSecurity) {
        log.info("Enabling web security")
        val baseLoginUri = securityProperties.loginUrl.replace("{registrationId}", "**")
        val requestCache = RedirectUriAwareCookieServerRequestCache(securityProperties.allowedRedirectUris)
        val securityContextRepository = WebSessionServerSecurityContextRepository()
        val securityContextLogoutHandler = SecurityContextServerLogoutHandler()
        securityContextLogoutHandler.setSecurityContextRepository(securityContextRepository)
        val logoutHandler = DelegatingServerLogoutHandler(
            securityContextLogoutHandler,
            WebSessionServerLogoutHandler()
        )
        //@formatter:off
        http
            .securityContextRepository(securityContextRepository)
            .requestCache()
                .requestCache(requestCache)
                .and()
            .authorizeExchange()
                .pathMatchers("/actuator/health/**").permitAll()
                .pathMatchers(baseLoginUri).permitAll()
                .anyExchange().authenticated()
                .and()
            .logout()
                .logoutHandler(logoutHandler)
                .requiresLogout(PathPatternParserServerWebExchangeMatcher(securityProperties.logoutUrl))
                .and()
            .oauth2Login()
                .authenticationSuccessHandler(buildSuccessHandler(sessionProperties, requestCache))
                .authenticationFailureHandler(AmbassadorAuthenticationFailureHandler)
                .clientRegistrationRepository(oauthRegistrationRepository)
                .authorizationRequestResolver(if (securityProperties.redirectOnUnauthenticated) {
                    DefaultServerOAuth2AuthorizationRequestResolver(oauthRegistrationRepository, PathPatternParserServerWebExchangeMatcher(securityProperties.loginUrl))
                } else {
                    RedirectUriAwareOAuth2AuthorizationRequestResolver(requestCache, securityProperties.loginUrl, oauthRegistrationRepository)
                })
        //@formatter:on


        // TODO wpada w redirect loop gdy uderzanie pod login bez redirect_uri

        val entryPoint = with(getLoginLocation()) {
            if (securityProperties.redirectOnUnauthenticated) {
                val ep = RedirectServerAuthenticationEntryPoint(this)
                ep.setRequestCache(requestCache)
                ep
            } else {
                HttpStatusWithLocationAuthenticationEntryPoint(this)
            }
        }
        http.exceptionHandling()
            .authenticationEntryPoint(entryPoint)
    }

    private fun getLoginLocation(): String {
        // TODO fix in future to support multiple oauth locations!
        val loginRegistration = oauthRegistrationRepository
            .first { it.authorizationGrantType == AuthorizationGrantType.AUTHORIZATION_CODE }
        return securityProperties.loginUrl.replace("{registrationId}", loginRegistration.registrationId)
    }

    private fun buildSuccessHandler(sessionProperties: SessionProperties, requestCache: ServerRequestCache): ServerAuthenticationSuccessHandler {
        val redirectSuccessHandler = RedirectServerAuthenticationSuccessHandler()
        redirectSuccessHandler.setRequestCache(requestCache)
        return DelegatingServerAuthenticationSuccessHandler(
            SessionConfiguringAuthenticationSuccessHandler(sessionProperties), LoggingAuthenticationSuccessHandler,
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
