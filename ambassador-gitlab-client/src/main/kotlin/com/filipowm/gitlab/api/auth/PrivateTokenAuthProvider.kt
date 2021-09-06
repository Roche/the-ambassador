package com.filipowm.gitlab.api.auth

import java.util.*

class PrivateTokenAuthProvider(private val token: String) : HeaderAuthProvider() {

    override fun header(): String = "PRIVATE-TOKEN"

    override suspend fun getToken(): Optional<String> = Optional.of(token)
}
