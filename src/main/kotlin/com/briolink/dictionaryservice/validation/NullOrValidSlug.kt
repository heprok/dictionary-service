package com.briolink.dictionaryservice.validation

import org.hibernate.validator.constraints.CompositionType
import org.hibernate.validator.constraints.ConstraintComposition
import javax.validation.Constraint
import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Null
import kotlin.reflect.KClass

@ConstraintComposition(CompositionType.OR)
@Constraint(validatedBy = [])
@ReportAsSingleViolation
@Null
@ValidSlug
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class NullOrValidSlug(
    val message: String = "validation.slug.null-or-valid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<*>> = []
)
