package com.filipowm.ambassador

data class OAuth2ClientProperties(
    val name: String,
    val authorizationUri: String,
    val jwkSetUri: String,
    val tokenUri: String,
    val userInfoUri: String,
    val usernameAttributeName: String,
    val scopes: Set<String>
)
