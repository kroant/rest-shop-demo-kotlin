package cz.kromer.restshopdemo.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class OrderItemDto(

    @field:Valid
    @field:NotNull
    val product: OrderProductDto?,

    @field:NotNull
    @field:Positive
    @field:Digits(integer = 16, fraction = 3)
    val amount: BigDecimal?
)