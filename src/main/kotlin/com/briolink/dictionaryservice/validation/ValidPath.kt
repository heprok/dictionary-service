package com.briolink.dictionaryservice.validation

import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Pattern
import kotlin.reflect.KClass

@ReportAsSingleViolation
@Constraint(validatedBy = [])
@Pattern(
    regexp = "^(\\w{1,256}\\.?){1,65535}\$"
)
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FIELD, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CLASS
)
annotation class ValidPath(
    val message: String = "validation.path.invalid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<Payload>> = [],
)
