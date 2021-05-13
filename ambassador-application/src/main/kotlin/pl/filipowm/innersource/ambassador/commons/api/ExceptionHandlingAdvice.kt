package pl.filipowm.innersource.ambassador.commons.api

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ResponseStatusException
import pl.filipowm.innersource.ambassador.commons.exceptions.NotFoundException
import pl.filipowm.innersource.ambassador.commons.validation.ValidationError
import javax.validation.ConstraintViolationException


@RestControllerAdvice
class ExceptionHandlingAdvice {

    val log = LoggerFactory.getLogger(ExceptionHandlingAdvice::class.java)

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun resourceNotFoundException(ex: NotFoundException): ErrorMessage? {
        return ErrorMessage("Not found")
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun x(ex: ResponseStatusException): ResponseEntity<ErrorMessage?> {
        log.error("Error occured", ex)
        val msg = ErrorMessage(
            "${ex.reason}"
        )
        return ResponseEntity
            .status(ex.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(msg)
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun unexpectedError(ex: Throwable): ErrorMessage? {
        log.error("Error occured", ex)
        return ErrorMessage("Unexpected issue occurred")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ValidationError =
        ValidationError.from(ex.bindingResult)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException::class)
    fun xe(ex: WebExchangeBindException): ValidationError = ValidationError.from(ex.bindingResult)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException::class)
    fun cve(ex: ConstraintViolationException) : ValidationError = ValidationError.from(ex.constraintViolations)
}