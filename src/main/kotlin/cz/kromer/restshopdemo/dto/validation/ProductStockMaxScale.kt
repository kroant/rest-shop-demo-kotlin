package cz.kromer.restshopdemo.dto.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.reflect.KClass

@Target(CLASS)
@Retention(RUNTIME)
@Constraint(validatedBy = [ProductStockMaxScaleValidator::class])
annotation class ProductStockMaxScale(

    val message: String = "{validation.ProductStockMaxScale.message}",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)
