package com.filipowm.ambassador.commons.validation

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import java.util.stream.Collectors
import javax.validation.ConstraintViolation

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class ValidationError(
    val message: String,
    val total: Int,
    val fieldErrors: Map<String, String?>,
    val globalErrors: List<String?>
) {

    companion object {
        fun just(message: String, field: String, error: String): ValidationError {
            return ValidationError(message, 1, mapOf(Pair(field, error)), listOf())
        }

        fun from(bindingResult: BindingResult): ValidationError {
            val fieldErrors = bindingResult.fieldErrors
                .stream()
                .collect(
                    Collectors.toMap(
                        FieldError::getField, FieldError::getDefaultMessage
                    )
                )
            val globalErrors = bindingResult.globalErrors
                .map { it.defaultMessage }
            return ValidationError(
                "Failed validating input",
                bindingResult.errorCount,
                fieldErrors.toMap(),
                globalErrors
            )
        }

        fun from(constraintViolations: Set<ConstraintViolation<*>>): ValidationError {
            val fieldErrors = constraintViolations.stream()
                .collect(Collectors.toMap(
                    { it.propertyPath.toString() }, { it.message }
                ))
            return ValidationError(
                "Failed validating input",
                fieldErrors.size,
                fieldErrors.toMap(),
                emptyList()
            )
        }
    }
}
