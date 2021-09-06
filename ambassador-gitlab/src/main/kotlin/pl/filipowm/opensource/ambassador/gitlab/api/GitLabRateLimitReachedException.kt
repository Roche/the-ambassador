package pl.filipowm.opensource.ambassador.gitlab.api

import pl.filipowm.opensource.ambassador.exceptions.Exceptions

class GitLabRateLimitReachedException(
    val limit: Int,
    val observed: Int,
    val retryAfterSeconds: Int,
    val resetAtMillis: Long,
    message: String?
) : Exceptions.RateLimitReachedException(message)
