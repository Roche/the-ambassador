package com.roche.gitlab.api.auth

import io.ktor.client.features.auth.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.auth.*
import java.util.*

abstract class HeaderAuthProvider : AuthProvider {

    protected abstract suspend fun getToken(): Optional<String>
    protected open fun header(): String = HttpHeaders.Authorization
    override val sendWithoutRequest: Boolean
        get() = true

    override suspend fun addRequestHeaders(request: HttpRequestBuilder) {
        getToken().ifPresent { token ->
            request.headers {
                if (contains(header())) {
                    remove(header())
                }
                append(header(), token)
            }
        }
    }

    override fun isApplicable(auth: HttpAuthHeader): Boolean = true
}
