package com.roche.ambassador.security.configuration

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

internal class HttpStatusWithLocationAuthenticationEntryPoint(private val loginLocation: String,
                                                     private val httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED) : ServerAuthenticationEntryPoint {

    companion object {
        val LOGIN_HEADER_NAME = "X-Login-Location"
    }

    override fun commence(exchange: ServerWebExchange, authException: AuthenticationException): Mono<Void> {
        return Mono.fromRunnable {
            exchange.response.statusCode = httpStatus
            exchange.response.headers[LOGIN_HEADER_NAME] = loginLocation
        }
    }

}
