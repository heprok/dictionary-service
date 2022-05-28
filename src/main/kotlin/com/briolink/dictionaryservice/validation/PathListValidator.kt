package com.briolink.dictionaryservice.validation

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER)
@MustBeDocumented
@Constraint(validatedBy = [PathListValidator::class])
annotation class PathList(
    val message: String = "validation.path-list.invalid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class PathListValidator : ConstraintValidator<PathList, Set<String>> {
    override fun isValid(
        value: Set<String>?,
        context: ConstraintValidatorContext?
    ): Boolean {
        return value == null || value.all { it.matches(ValidPath.pattern.toRegex()) }
    }
}
