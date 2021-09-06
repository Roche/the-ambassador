package pl.filipowm.opensource.ambassador.gitlab.api

import org.gitlab4j.api.GitLabApiClient
import org.gitlab4j.api.GitLabApiClientDecorator
import pl.filipowm.opensource.ambassador.extensions.LoggerDelegate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import javax.ws.rs.core.Response

class RateLimitHandlerDecorator(wrapped: GitLabApiClient) : GitLabApiClientDecorator(wrapped) {

    private var rateLimitingLock = ReentrantLock()
    private var isRateLimitedCondition = rateLimitingLock.newCondition()
    private var isNotRateLimitedCondition = rateLimitingLock.newCondition()
    private var unlockScheduler = Executors.newSingleThreadScheduledExecutor()
    private var rateLimited = AtomicBoolean(false)

    companion object {
        private val log by LoggerDelegate()
    }

    override fun decorate(handler: () -> Response): Response {
        try {
//            rateLimitingLock.lock()
//            try {
//                while(rateLimited.get()) {
//                    isRateLimitedCondition.await()
//                }
//            } finally {
//                rateLimitingLock.unlock()
//            }
            val response = handler()

            return response
        } catch (ex: GitLabRateLimitReachedException) {
//            rateLimitingLock.lock()
            log.warn(
                "Rate limits reached (limit={}, observed={}, retryAfterSeconds={}, resetAt={})", ex.limit, ex.observed, ex.retryAfterSeconds,
                LocalDateTime.ofInstant(Instant.ofEpochMilli(ex.resetAtMillis), ZoneId.systemDefault())
            )
//            rateLimited.set(true)
//            unlockScheduler.schedule({ unlock() }, ex.retryAfterSeconds.toLong(), TimeUnit.SECONDS)
            throw ex
//            return handler()
        }
    }

    private fun unlock() {
        rateLimited.set(false)
        isRateLimitedCondition.signalAll()
        rateLimitingLock.unlock()
    }
}
