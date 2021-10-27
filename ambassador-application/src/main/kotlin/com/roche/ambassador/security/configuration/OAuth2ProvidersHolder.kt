package com.roche.ambassador.security.configuration

import com.roche.ambassador.OAuth2AuthenticationProvider
import org.springframework.security.oauth2.client.registration.ClientRegistration

internal class OAuth2ProvidersHolder(private val providers: MutableMap<ClientRegistration, OAuth2AuthenticationProvider> = mutableMapOf()) {

    fun add(clientRegistration: ClientRegistration, oAuth2AuthenticationProvider: OAuth2AuthenticationProvider) {
        providers.putIfAbsent(clientRegistration, oAuth2AuthenticationProvider)
    }

    fun isEmpty() = providers.isEmpty()

    fun get(clientRegistration: ClientRegistration) = providers[clientRegistration]
}
