package com.filipowm.ambassador.security.configuration

import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.extensions.toHumanReadable
import org.springframework.boot.autoconfigure.session.SessionProperties
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.web.server.WebSession
import reactor.core.publisher.Mono

internal sealed class AmbassadorAuthSuccessHandler : ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(webFilterExchange: WebFilterExchange, authentication: Authentication): Mono<Void> {
        onAuthenticationSuccessHandler().invoke(webFilterExchange, authentication)
        return Mono.empty()
    }

    abstract fun onAuthenticationSuccessHandler(): (WebFilterExchange, Authentication) -> Any
}

internal class SessionConfiguringAuthenticationSuccessHandler(private val sessionProperties: SessionProperties) : AmbassadorAuthSuccessHandler() {

    private val log by LoggerDelegate()

    init {
        log.info("Configured session duration to {}", sessionProperties.timeout.toHumanReadable())
    }

    override fun onAuthenticationSuccessHandler() = { webFilterExchange: WebFilterExchange, _: Authentication ->
        webFilterExchange.exchange.session.subscribe { session: WebSession ->
            session.maxIdleTime = sessionProperties.timeout
        }
    }
}

internal object LoggingAuthenticationSuccessHandler : AmbassadorAuthSuccessHandler() {

    private val log by LoggerDelegate()

    override fun onAuthenticationSuccessHandler() = { _: WebFilterExchange, authentication: Authentication ->
        log.info("Logged in as '{}' with authorities {}", authentication.name, authentication.authorities)
    }
}
