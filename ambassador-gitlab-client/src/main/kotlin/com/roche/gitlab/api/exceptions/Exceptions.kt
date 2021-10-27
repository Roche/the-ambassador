package com.roche.gitlab.api.exceptions

import io.ktor.client.features.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.util.*

object Exceptions {

    fun getExceptionForResponse(responseException: ResponseException): Optional<GitLabApiException> {
        val status = responseException.response.status.value
        if (status >= 500) {
            return Optional.of(ServerErrorException("Remote server responded with error, status=$status. Request: ${responseException.response.request.url}"))
        }
        return Optional.ofNullable(
            when (status) {
                401 -> UnauthorizedException("Lack of valid credentials.", null, status, responseException.response.headers)
                403 -> ForbiddenException("Insufficient privileges to access requested resource.", null, status, responseException.response.headers)
                404 -> NotFoundException("Requested data was not found.", null, status, responseException.response.headers)
                408 -> RequestTimeoutException("Request timed out.", null, status, responseException.response.headers)
                429 -> RateLimitReachedException("Rate limit was reached. Please wait and retry later.", responseException, status, responseException.response.headers)
                else -> GitLabApiException("Unknown error occurred due to issue on client side.", responseException, status, responseException.response.headers)
            }
        )
    }

    open class GitLabApiException(message: String?, cause: Throwable? = null, val status: Int? = null, val headers: Headers = Headers.Empty) : RuntimeException(message, cause)
    open class RateLimitReachedException(message: String?, cause: Throwable? = null, status: Int? = null, headers: Headers = Headers.Empty) :
        GitLabApiException(message, cause, status, headers)

    open class UnauthorizedException(message: String?, cause: Throwable? = null, status: Int? = null, headers: Headers = Headers.Empty) :
        GitLabApiException(message, cause, status, headers)

    open class ForbiddenException(message: String?, cause: Throwable? = null, status: Int? = null, headers: Headers = Headers.Empty) :
        GitLabApiException(message, cause, status, headers)

    open class NotFoundException(message: String?, cause: Throwable? = null, status: Int? = null, headers: Headers = Headers.Empty) :
        GitLabApiException(message, cause, status, headers)

    open class RequestTimeoutException(message: String?, cause: Throwable? = null, status: Int? = null, headers: Headers = Headers.Empty) :
        GitLabApiException(message, cause, status, headers)

    open class ServerErrorException(message: String?, cause: Throwable? = null, status: Int? = null, headers: Headers = Headers.Empty) :
        GitLabApiException(message, cause, status, headers)
}
