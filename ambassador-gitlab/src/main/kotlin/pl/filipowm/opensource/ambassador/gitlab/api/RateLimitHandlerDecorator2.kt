package pl.filipowm.opensource.ambassador.gitlab.api

import org.gitlab4j.api.GitLabApiClient
import org.gitlab4j.api.GitLabApiClientDecorator
import org.slf4j.LoggerFactory
import javax.ws.rs.core.Response

class RateLimitHandlerDecorator2(
    wrapped: GitLabApiClient,
) : GitLabApiClientDecorator(wrapped) {

    companion object {
        private val log = LoggerFactory.getLogger(RateLimitHandlerDecorator2::class.java)
    }

    override fun decorate(handler: () -> Response): Response {
//        val rtb = RetryTemplateBuilder()
//            .retryOn(GitLabRateLimitReachedException::class.java)
//            .fixedBackoff(5000)
//            .
//        retryTemplate.execute(RetryCallback<Unit, GitLabRateLimitReachedException> {  })
//        try {
//            val response = handler()
//
//            return response
//        } catch (ex: GitLabRateLimitReachedException) {
//            log.warn(
//                "Rate limits reached (limit={}, observed={}, retryAfterSeconds={}, resetAt={})", ex.limit, ex.observed, ex.retryAfterSeconds,
//                LocalDateTime.ofInstant(Instant.ofEpochMilli(ex.resetAtMillis), ZoneId.systemDefault())
//            )
//            throw ex
//        }
        return handler()
    }
}
