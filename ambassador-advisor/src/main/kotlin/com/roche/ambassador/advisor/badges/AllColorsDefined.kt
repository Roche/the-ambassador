package com.roche.ambassador.advisor.badges

import com.roche.ambassador.advisor.common.Color
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [AllColorsDefinedValidator::class])
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class AllColorsDefined(
    val message: String = "Not all colors were defined",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

internal class AllColorsDefinedValidator : ConstraintValidator<AllColorsDefined, Map<Color, String>> {
    override fun isValid(value: Map<Color, String>, context: ConstraintValidatorContext): Boolean {
        context.disableDefaultConstraintViolation()
        val missingColors = mutableListOf<Color>()
        for (color in Color.values()) {
            if (!value.containsKey(color) || value[color].isNullOrBlank()) {
                missingColors += color
            }
        }
        if (missingColors.isNotEmpty()) {
            context
                .buildConstraintViolationWithTemplate("Colors ${missingColors.joinToString(",") { it.name }} are not defined")
                .addConstraintViolation()
        }
        return missingColors.isEmpty()
    }
}
