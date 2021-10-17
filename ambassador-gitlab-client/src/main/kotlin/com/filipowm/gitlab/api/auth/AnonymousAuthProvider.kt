package com.filipowm.gitlab.api.auth

import io.ktor.client.features.auth.*
import io.ktor.client.request.*
import io.ktor.http.auth.*

object AnonymousAuthProvider : AuthProvider {
    override val sendWithoutRequest: Boolean
        get() = true

    override suspend fun addRequestHeaders(request: HttpRequestBuilder) {
        // not headers to add for anonymous auth
    }

    override fun isApplicable(auth: HttpAuthHeader): Boolean = true
}
