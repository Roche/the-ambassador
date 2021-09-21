package com.filipowm.ambassador.security

import org.springframework.security.core.context.SecurityContextHolder
import java.lang.IllegalStateException
import java.util.*

object AuthenticationContext {

    fun currentUser(): AmbassadorUser {
        val ctx = SecurityContextHolder.getContext();
        val md = SecurityContextHolder.getContextHolderStrategy()
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map { it.authentication }
            .map { it.principal }
            .filter { it is AmbassadorUser }
            .map { it as AmbassadorUser }
            .orElseThrow { IllegalStateException("Expected valid authentication to exist") }
    }

    fun currentUserName(): String {
        return currentUser().username
    }
}