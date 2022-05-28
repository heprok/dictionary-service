package com.briolink.dictionaryservice.validation

import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import kotlin.reflect.KClass

@ReportAsSingleViolation
@Constraint(validatedBy = [])
@Pattern(
    regexp = "^[a-z\\-_\\d]+\$"
)
@Size(min = 1, max = 255, message = "validation.slug.size") // Size must be between 1 and 255
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FIELD, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CLASS
)
annotation class ValidSlug(
    val message: String = "validation.slug.invalid", // "uuid.invalid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<Payload>> = [],
)
