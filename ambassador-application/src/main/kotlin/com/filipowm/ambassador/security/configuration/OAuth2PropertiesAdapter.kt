package com.filipowm.ambassador.security.configuration

import com.filipowm.ambassador.Adapter
import com.filipowm.ambassador.OAuth2ClientProperties
import com.filipowm.ambassador.extensions.toCamelCase
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType

internal object OAuth2PropertiesAdapter : Adapter<OAuth2ClientProperties, ClientRegistration> {
    override fun convert(props: OAuth2ClientProperties?): ClientRegistration {
        return ClientRegistration.withRegistrationId(props!!.name.toCamelCase())
            .authorizationUri(props.authorizationUri)
            .jwkSetUri(props.jwkSetUri)
            .tokenUri(props.tokenUri)
            .userInfoUri(props.userInfoUri)
            .userNameAttributeName(props.usernameAttributeName)
            .scope(props.scopes)
            .clientName(props.name)
            .clientId("__dummy__")
            .clientSecret("__dummy__")
            .redirectUri("__dummy__")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // TODO no other supported yet
            .build()
    }
}
