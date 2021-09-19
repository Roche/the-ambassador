package com.filipowm.ambassador.commons.api

import com.filipowm.ambassador.commons.validation.ValidationError
import com.filipowm.ambassador.exceptions.Exceptions.NotFoundException
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.project.indexer.IndexingAlreadyStartedException
import com.filipowm.ambassador.storage.InvalidSortFieldException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.security.Principal
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class ExceptionHandlingAdvice {

    private val log by LoggerDelegate()

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun resourceNotFoundException(ex: NotFoundException): Message {
        val message = if (ex.message != null) ex.message else "Not found"
        return Message(message!!)
    }

    @ExceptionHandler(InvalidSortFieldException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun invalidSorting(ex: InvalidSortFieldException): ValidationError = ValidationError.just(ex.message!!, ex.field, "Field does not exist")

    @ExceptionHandler(ResponseStatusException::class)
    fun responseStatusException(ex: ResponseStatusException): ResponseEntity<Message?> {
        log.error("Error occurred", ex)
        val msg = Message(
            "${ex.reason}"
        )
        return ResponseEntity
            .status(ex.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(msg)
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun unexpectedError(ex: Throwable): Message {
        log.error("Error occurred", ex)
        return Message("Unexpected issue occurred")
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun forbidden(ex: AccessDeniedException, exchange: ServerWebExchange): Mono<Message> {
        return exchange.getPrincipal<Principal>()
            .map { it.name }
            .doOnNext { log.warn("User '{}' attempted to access '{}' without needed permissions", it, exchange.request.path)  }
            .map { Message("Access Denied")}
    }

    @ExceptionHandler(IndexingAlreadyStartedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun indexingConflict(ex: IndexingAlreadyStartedException): Message {
        log.warn(ex.message)
        return Message(ex.message!!)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ValidationError =
        ValidationError.from(ex.bindingResult)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException::class)
    fun bindingException(ex: WebExchangeBindException): ValidationError = ValidationError.from(ex.bindingResult)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintViolationException(ex: ConstraintViolationException): ValidationError = ValidationError.from(ex.constraintViolations)
}
