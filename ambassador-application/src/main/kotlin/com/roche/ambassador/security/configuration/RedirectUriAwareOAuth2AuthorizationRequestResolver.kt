package com.roche.ambassador.security.configuration

import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

internal class RedirectUriAwareOAuth2AuthorizationRequestResolver(
    private val cache: RedirectUriAwareCookieServerRequestCache,
    loginUrl: String,
    repo: ReactiveClientRegistrationRepository
) : DefaultServerOAuth2AuthorizationRequestResolver(repo, PathPatternParserServerWebExchangeMatcher(loginUrl)) {

    override fun resolve(exchange: ServerWebExchange): Mono<OAuth2AuthorizationRequest>? {
        return super.resolve(exchange)
            .flatMap { exchange.saveWhenEligible().then(Mono.just(it)) }
    }

    private fun ServerWebExchange.saveWhenEligible(): Mono<Void> {
        val redirectUris = this.request.queryParams.getOrDefault("redirect_uri", listOf())
        return if (redirectUris.isNotEmpty()) {
            cache.saveRequest(this)
        } else {
            Mono.empty()
        }
    }

}