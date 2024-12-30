package cz.kromer.restshopdemo.dto

import cz.kromer.restshopdemo.dto.validation.UniqueOrderProduct
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class CreateOrderDto(

    @field:NotEmpty
    @field:UniqueOrderProduct
    val items: List<@Valid @NotNull OrderItemDto?>?
)