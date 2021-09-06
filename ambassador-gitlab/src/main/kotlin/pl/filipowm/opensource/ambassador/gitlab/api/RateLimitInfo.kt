package pl.filipowm.opensource.ambassador.gitlab.api

import java.util.*
import javax.ws.rs.core.Response

data class RateLimitInfo(
    val limit: Int,
    val observed: Int,
    val remaining: Int,
    val retryAfterSeconds: Int,
    val resetAtMillis: Long
) {

    companion object {
        fun from(response: Response): RateLimitInfo {
            val limit = response.getHeaderAsInt(RATE_LIMIT_LIMIT).orElse(Int.MAX_VALUE)
            val observed = response.getHeaderAsInt(RATE_LIMIT_OBSERVED).orElse(0)
            val remaining = response.getHeaderAsInt(RATE_LIMIT_REMAINING).orElse(0)
            val retryAfterSeconds = response.getHeaderAsInt(RATE_LIMIT_RETRY_AFTER).orElse(0)
            val resetAtMillis = response.getHeaderAsLong(RATE_LIMIT_RESET).orElse(0)
            return RateLimitInfo(limit, observed, remaining, retryAfterSeconds, resetAtMillis)
        }

        private const val RATE_LIMIT_LIMIT = "RateLimit-Limit"
        private const val RATE_LIMIT_NAME = "RateLimit-Name"
        private const val RATE_LIMIT_OBSERVED = "RateLimit-Observed"
        private const val RATE_LIMIT_REMAINING = "RateLimit-Remaining"
        private const val RATE_LIMIT_RESET = "RateLimit-Reset"
        private const val RATE_LIMIT_RESET_TIME = "RateLimit-ResetTime"
        private const val RATE_LIMIT_RETRY_AFTER = "Retry-After"

        private fun Response.getHeaderAsLong(name: String): Optional<Long> {
            return getHeader(name, String::toLongOrNull)
        }

        private fun Response.getHeaderAsInt(name: String): Optional<Int> {
            return getHeader(name, String::toIntOrNull)
        }

        private fun <T> Response.getHeader(name: String, transformer: (String) -> T?): Optional<T> {
            val value = this.getHeaderString(name)
            if (value != null) {
                val transformed = transformer.invoke(value)
                return Optional.ofNullable(transformed)
            }
            return Optional.empty()
        }
    }
}
