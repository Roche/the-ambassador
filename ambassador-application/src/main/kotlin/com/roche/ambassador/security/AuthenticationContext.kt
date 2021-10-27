package com.roche.ambassador.security

import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import java.util.*

object AuthenticationContext {

    suspend fun getContext(): Optional<SecurityContext> {
        return Optional.ofNullable(ReactiveSecurityContextHolder.getContext().awaitFirstOrNull())
    }

    suspend fun currentUser(): Optional<AmbassadorUser> {
        return getContext()
            .map { it.authentication }
            .map { it.principal }
            .filter { it is AmbassadorUser }
            .map { it as AmbassadorUser }
    }

    suspend fun currentUserName(): Optional<String> {
        return currentUser().map { it.username }
    }

    suspend fun currentUserNameOrElse(default: String = "unknown"): String {
        return currentUserName().orElse(default)
    }
}