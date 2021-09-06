package com.filipowm.gitlab.api.auth

import io.ktor.client.features.auth.*
import io.ktor.client.request.*
import io.ktor.http.auth.*

object AnonyomusAuthProvider : AuthProvider {
    override val sendWithoutRequest: Boolean
        get() = true

    override suspend fun addRequestHeaders(request: HttpRequestBuilder) {
    }

    override fun isApplicable(auth: HttpAuthHeader): Boolean = true
}
