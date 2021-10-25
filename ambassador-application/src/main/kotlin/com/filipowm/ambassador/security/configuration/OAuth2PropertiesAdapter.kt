package com.filipowm.ambassador.security.configuration

import com.filipowm.ambassador.Adapter
import com.filipowm.ambassador.OAuth2ClientProperties
import com.filipowm.ambassador.extensions.toCamelCase
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType

internal object OAuth2PropertiesAdapter : Adapter<OAuth2ClientProperties, ClientRegistration> {
    override fun convert(value: OAuth2ClientProperties?): ClientRegistration {
        return ClientRegistration.withRegistrationId(value!!.name.toCamelCase())
            .authorizationUri(value.authorizationUri)
            .jwkSetUri(value.jwkSetUri)
            .tokenUri(value.tokenUri)
            .userInfoUri(value.userInfoUri)
            .userNameAttributeName(value.usernameAttributeName)
            .scope(value.scopes)
            .clientName(value.name)
            .clientId("__dummy__")
            .clientSecret("__dummy__")
            .redirectUri("__dummy__")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // TODO no other supported yet
            .build()
    }
}
