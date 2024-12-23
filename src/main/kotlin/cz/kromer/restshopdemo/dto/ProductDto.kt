package cz.kromer.restshopdemo.dto

import java.math.BigDecimal
import java.util.UUID

data class ProductDto(
    val id: UUID,
    val name: String,
    val unit: QuantityUnit,
    val price: BigDecimal,
    val stock: BigDecimal
)
