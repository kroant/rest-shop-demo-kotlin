package cz.kromer.restshopdemo.dto.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.reflect.KClass

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = [UniqueOrderProductValidator::class])
annotation class UniqueOrderProduct(

    val message: String = "{validation.UniqueOrderProduct.message}",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)
