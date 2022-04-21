package com.roche.ambassador.configuration.web

import com.roche.ambassador.commons.api.Message
import com.roche.ambassador.configuration.i18n.ReactiveMessageSource
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

internal class GlobalErrorWebExceptionHandler(
    private val messageSource: ReactiveMessageSource,
    serverCodecConfigurer: ServerCodecConfigurer,
    errorAttributes: ErrorAttributes,
    resources: WebProperties,
    applicationContext: ApplicationContext
) : AbstractErrorWebExceptionHandler(errorAttributes, resources.resources, applicationContext) {

    init {
        setMessageWriters(serverCodecConfigurer.writers)
        setMessageReaders(serverCodecConfigurer.readers)
    }

    override fun getRoutingFunction(
        errorAttributes: ErrorAttributes
    ): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.all()) {
            runBlocking {
                renderErrorResponse(it)
            }
        }
    }

    private suspend fun renderErrorResponse(
        request: ServerRequest
    ): Mono<ServerResponse> {
        val errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults())
        val status = errorPropertiesMap["status"] as Int
        val msg = messageSource.getMessage("web.errors.$status", "Request processing has failed")
        val me = Message(msg)
        return ServerResponse.status(status).json().body(BodyInserters.fromValue(me))
    }
}
