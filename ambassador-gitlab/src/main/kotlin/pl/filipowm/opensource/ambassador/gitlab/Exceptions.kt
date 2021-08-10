package pl.filipowm.opensource.ambassador.gitlab

import java.util.*

object Exceptions {

    fun getExceptionForStatus(status: Int): Optional<GitlabIndexerException> {
        if (status >= 500) {
            return Optional.of(ServerErrorException("Remote server responded with error, status=$status"))
        }
        return Optional.ofNullable(
            when (status) {
                401 -> UnauthorizedException("No credentials provided.")
                403 -> ForbiddenException("Insufficient privileges to access requested resource.")
                404 -> NotFoundException("Requested data was not found.")
                408 -> RequestTimeoutException("Request timed out.")
                429 -> RateLimitReachedException("Rate limit was reached. Please wait and retry later.")
                else -> null
            }
        )
    }

    class RateLimitReachedException(message: String?, cause: Throwable? = null) : GitlabIndexerException(message, cause)
    class UnauthorizedException(message: String?, cause: Throwable? = null) : GitlabIndexerException(message, cause)
    class ForbiddenException(message: String?, cause: Throwable? = null) : GitlabIndexerException(message, cause)
    class NotFoundException(message: String?, cause: Throwable? = null) : GitlabIndexerException(message, cause)
    class RequestTimeoutException(message: String?, cause: Throwable? = null) : GitlabIndexerException(message, cause)
    class ServerErrorException(message: String?, cause: Throwable? = null) : GitlabIndexerException(message, cause)

}