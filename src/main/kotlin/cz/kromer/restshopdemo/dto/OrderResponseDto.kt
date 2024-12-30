package cz.kromer.restshopdemo.dto

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class OrderResponseDto(

    val id: UUID,
    val state: OrderState,
    val price: BigDecimal,
    val items: List<OrderItemDto>,
    val createdOn: Instant
)