package cz.kromer.restshopdemo.exception

import cz.kromer.restshopdemo.dto.OrderProductDto
import java.math.BigDecimal

data class ProductStockShortageDto(
    val product: OrderProductDto,
    val missingAmount: BigDecimal
)