package com.roche.ambassador

interface OAuth2AuthenticationProvider {

    fun getOAuth2ClientProperties(): OAuth2ClientProperties?
    fun userDetailsProvider(attributes: Map<String, Any>): UserDetailsProvider?
    fun isSupported(): Boolean = getOAuth2ClientProperties() != null && userDetailsProvider(mapOf()) != null
}
