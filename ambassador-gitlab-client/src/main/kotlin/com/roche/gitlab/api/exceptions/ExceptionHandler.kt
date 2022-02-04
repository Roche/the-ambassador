package com.roche.gitlab.api.exceptions

import io.ktor.client.features.*

interface ExceptionHandler {
    suspend fun handle(exception: ResponseException)

    companion object {
        fun delegatingTo(handlers: Map<Int, ExceptionHandler>): ExceptionHandler = DelegatingExceptionHandler(handlers)
    }
}

private class DelegatingExceptionHandler(private val handlers: Map<Int, ExceptionHandler> = mapOf()) : ExceptionHandler {

    override suspend fun handle(exception: ResponseException) {
        val status = exception.response.status.value
        handlers.getOrDefault(status, WrappingExceptionHandler).handle(exception)
    }

    object WrappingExceptionHandler : ExceptionHandler {
        override suspend fun handle(exception: ResponseException) {
            throw Exceptions.getExceptionForResponse(exception)
        }
    }
}
