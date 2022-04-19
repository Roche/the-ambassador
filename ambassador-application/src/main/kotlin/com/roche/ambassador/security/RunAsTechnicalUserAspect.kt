package com.roche.ambassador.security

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component

@Aspect
@Component
internal open class RunAsTechnicalUserAspect {

    companion object {
        val technicalUser = AmbassadorUser(
            "Technical User",
            "_admin", "admin@admin.com",
            mapOf(), listOf("ROLE_ADMIN", "ROLE_USER")
        )
        private val technicalUserToken = TechnicalUserToken(technicalUser)
    }

    private class TechnicalUserToken(private val ambassadorUser: AmbassadorUser) : AbstractAuthenticationToken(ambassadorUser.authorities) {
        override fun getCredentials(): Any = "__none__"

        override fun getPrincipal(): AmbassadorUser = ambassadorUser

    }

    @Around("@annotation(com.roche.ambassador.security.RunAsTechnicalUser)")
    open fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any? {
        val savedContext = ReactiveSecurityContextHolder.getContext()
        ReactiveSecurityContextHolder.withAuthentication(technicalUserToken)
        try {
            return joinPoint.proceed()
        } finally {
            if (savedContext != null) {
                ReactiveSecurityContextHolder.withSecurityContext(savedContext)
            } else {
                ReactiveSecurityContextHolder.clearContext()
            }
        }
    }
}