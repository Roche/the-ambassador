package com.roche.gitlab.api.exceptions

import io.ktor.client.features.*
import io.ktor.client.statement.*
import io.ktor.http.*

object Exceptions {

    fun getExceptionForResponse(responseException: ResponseException): GitLabApiException {
        val status = responseException.response.status.value
        if (status >= 500) {
            return ServerErrorException("Remote server responded with error, status=$status.", responseException)
        }
        return when (status) {
            401 -> UnauthorizedException("Lack of valid credentials.", responseException)
            403 -> ForbiddenException("Insufficient privileges to access requested resource.", responseException)
            404 -> NotFoundException("Requested data was not found.", responseException)
            408 -> RequestTimeoutException("Request timed out.", responseException)
            429 -> RateLimitReachedException("Rate limit was reached. Please wait and retry later.", responseException)
            else -> GitLabApiException("Unknown error occurred due to issue on client side.", responseException)
        }
    }

    open class GitLabApiException(
        message: String,
        responseException: ResponseException
    ) : RuntimeException("$message. URL: ${responseException.response.request.url}", responseException) {
        val status: Int = responseException.response.status.value
        val headers: Headers = responseException.response.headers
    }

    open class RateLimitReachedException(message: String, responseException: ResponseException) : GitLabApiException(message, responseException)

    open class UnauthorizedException(message: String, responseException: ResponseException) : GitLabApiException(message, responseException)

    open class ForbiddenException(message: String, responseException: ResponseException) : GitLabApiException(message, responseException)

    open class NotFoundException(message: String, responseException: ResponseException) : GitLabApiException(message, responseException)

    open class RequestTimeoutException(message: String, responseException: ResponseException) : GitLabApiException(message, responseException)

    open class ServerErrorException(message: String, responseException: ResponseException) : GitLabApiException(message, responseException)
}
