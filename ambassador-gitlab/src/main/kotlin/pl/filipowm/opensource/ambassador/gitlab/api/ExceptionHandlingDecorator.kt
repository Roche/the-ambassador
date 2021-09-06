package pl.filipowm.opensource.ambassador.gitlab.api

import org.gitlab4j.api.GitLabApiClient
import org.gitlab4j.api.GitLabApiClientDecorator
import pl.filipowm.opensource.ambassador.exceptions.Exceptions
import pl.filipowm.opensource.ambassador.extensions.LoggerDelegate
import java.net.ConnectException
import javax.ws.rs.core.Response

internal class ExceptionHandlingDecorator(wrapped: GitLabApiClient) : GitLabApiClientDecorator(wrapped) {

    companion object {
        private val log by LoggerDelegate()
    }

    override fun decorate(handler: () -> Response): Response {
        try {
            val response = handler()
            if (response.status >= 400) {
                handleRateLimiting(response)
                Exceptions.getExceptionForStatus(response.status).ifPresent { throw it }
            }
            return response
        } catch (e: ConnectException) {
            log.error("Failed making a request to GitLab due to '{}'", e.message)
            throw Exceptions.RequestTimeoutException("Timeout", e)
        }
    }

    private fun handleRateLimiting(response: Response) {
        if (response.status == 429) {
            val info = RateLimitInfo.from(response)
            throw GitLabRateLimitReachedException(info.limit, info.observed, info.retryAfterSeconds, info.resetAtMillis, "")
        }
    }
}
