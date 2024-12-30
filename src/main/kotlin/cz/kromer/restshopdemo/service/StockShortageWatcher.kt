package cz.kromer.restshopdemo.service

import cz.kromer.restshopdemo.entity.OrderItem
import cz.kromer.restshopdemo.exception.ProductShortageException
import cz.kromer.restshopdemo.exception.ProductStockShortageDto
import cz.kromer.restshopdemo.mapper.OrderProductDtoMapper
import org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.math.BigDecimal.ZERO
import java.util.LinkedList

@Component
@Scope(SCOPE_PROTOTYPE)
class StockShortageWatcher(
    private val orderProductDtoMapper: OrderProductDtoMapper,
) {
    private val shortages = LinkedList<ProductStockShortageDto>()

    fun take(orderItems: List<OrderItem>) {
        orderItems.forEach(::take)
        if (shortages.isNotEmpty()) {
            throw ProductShortageException(shortages)
        }
    }

    private fun take(item: OrderItem) {
        val product = item.product
        val newAmount = product.stock - item.amount
        if (newAmount < ZERO) {
            shortages += ProductStockShortageDto(
                product = orderProductDtoMapper.mapFrom(product),
                missingAmount = newAmount.negate()
            )
        } else {
            product.stock = newAmount
        }
    }
}