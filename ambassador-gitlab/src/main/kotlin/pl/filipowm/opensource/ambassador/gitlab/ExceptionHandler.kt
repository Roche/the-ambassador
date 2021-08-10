package pl.filipowm.opensource.ambassador.gitlab

import org.gitlab4j.api.GitLabApiException
import org.slf4j.LoggerFactory
import java.util.*

object ExceptionHandler {

    private val log = LoggerFactory.getLogger(ExceptionHandler::class.java)

    fun <T> withGitLabException(handler: () -> T): Optional<T> {
        try {
            return Optional.ofNullable(handler())
        } catch (ex: GitLabApiException) {
            log.error("Request to GitLab failed. Status: {}, cause: {}", ex.httpStatus, ex.reason)
            if (ex.hasValidationErrors()) {
                log.error("Found validation errors: {}", ex.validationErrors)
            }
            Exceptions.getExceptionForStatus(ex.httpStatus)
                .ifPresent { throw it }
            return Optional.empty()
        }
    }

}