package com.filipowm.ambassador.security.configuration

import com.filipowm.ambassador.OAuth2AuthenticationProvider
import com.filipowm.ambassador.configuration.source.ProjectSourcesProperties
import com.filipowm.ambassador.extensions.LoggerDelegate
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType

internal class ClientRegistrationRegistrar(private val projectSourcesProperties: ProjectSourcesProperties) {

    companion object {
        val DEFAULT_AUTHORIZATION_GRANT_TYPE = AuthorizationGrantType.AUTHORIZATION_CODE!!
    }

    private val log by LoggerDelegate()

    fun createRegistrations(oAuth2AuthenticationProviders: List<OAuth2AuthenticationProvider>): InMemoryReactiveClientRegistrationRepository {
        log.debug("Registering OAuth2 clients")
        val clientRegistrations = oAuth2AuthenticationProviders
            .asSequence()
            .filter { it.isSupported() }
            .map { it.getOAuth2ClientProperties() }
            .map { OAuth2PropertiesAdapter.convert(it) }
            .filter(this::isValid)
            .map(this::completeRegistration)
            .toList()

        if (clientRegistrations.isEmpty()) {
            throw IllegalStateException("No valid client registration")
        }
        log.info("Registered OAuth2 clients: {}", clientRegistrations.joinToString { it.clientName })
        return InMemoryReactiveClientRegistrationRepository(clientRegistrations)
    }

    private fun completeRegistration(partialRegistration: ClientRegistration): ClientRegistration {
        return ClientRegistration.withClientRegistration(partialRegistration)
            .clientSecret(projectSourcesProperties.clientSecret)
            .clientId(projectSourcesProperties.clientId)
            .redirectUri("http://localhost:8080/login/oauth2/code/gitlab")
            .authorizationGrantType(partialRegistration.authorizationGrantType ?: DEFAULT_AUTHORIZATION_GRANT_TYPE)
            .build()
    }

    private fun isValid(partialRegistration: ClientRegistration): Boolean {
        val errorCollector = ErrorCollector()
        errorCollector.verify(partialRegistration.scopes, "missing scopes")
        errorCollector.verify(partialRegistration.clientName, "missing clientName")
        if (partialRegistration.providerDetails == null) {
            errorCollector.addError("missing providerDetails")
        } else {
            isValid(partialRegistration.providerDetails, errorCollector)
        }
        if (errorCollector.hasErrors()) {
            log.warn("Registration {} has following errors:\n{}", errorCollector.getErrors().joinToString(prefix = "- ", separator = "\n"))
        }
        return !errorCollector.hasErrors()
    }

    private fun isValid(providerDetails: ClientRegistration.ProviderDetails, errorCollector: ErrorCollector) {
        errorCollector.verify(providerDetails.authorizationUri, "missing providerDetails.authorizationUri")
        errorCollector.verify(providerDetails.jwkSetUri, "missing providerDetails.jwkSetUri")
        errorCollector.verify(providerDetails.tokenUri, "missing providerDetails.tokenUri")
        if (providerDetails.userInfoEndpoint == null) {
            errorCollector.addError("missing providerDetails.userInfoEndpoint")
        } else {
            isValid(providerDetails.userInfoEndpoint, errorCollector)
        }
    }

    private fun isValid(userInfoEndpoint: ClientRegistration.ProviderDetails.UserInfoEndpoint, errorCollector: ErrorCollector) {
        errorCollector.verify(userInfoEndpoint.userNameAttributeName, "missing providerDetails.userInfoEndpoint.userNameAttributeName")
        errorCollector.verify(userInfoEndpoint.uri, "missing providerDetails.userInfoEndpoint.uri")
        errorCollector.verify(userInfoEndpoint.authenticationMethod, "missing providerDetails.userInfoEndpoint.authenticationMethod")
    }

    private class ErrorCollector {
        private val errors = mutableListOf<String>()

        fun verify(value: String?, message: String) {
            if (value.isNullOrBlank()) {
                addError(message)
            }
        }

        fun verify(value: Any?, message: String) {
            if (value == null) {
                addError(message)
            }
        }

        fun verify(value: Collection<*>?, message: String) {
            if (value.isNullOrEmpty()) {
                addError(message)
            }
        }

        fun addError(error: String) = errors.add(error)

        fun getErrors(): List<String> = listOf(*errors.toTypedArray()).sorted()

        fun hasErrors() = errors.isNotEmpty()
    }
}
