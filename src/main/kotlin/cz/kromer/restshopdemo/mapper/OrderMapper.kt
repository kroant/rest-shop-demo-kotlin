package cz.kromer.restshopdemo.mapper

import cz.kromer.restshopdemo.dto.CreateOrderDto
import cz.kromer.restshopdemo.dto.OrderProductDto
import cz.kromer.restshopdemo.entity.Order
import cz.kromer.restshopdemo.entity.Product
import org.mapstruct.AfterMapping
import org.mapstruct.Context
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget

@Mapper
abstract class OrderMapper {

    @Mapping(target = "state", constant = "NEW")
    @Mapping(target = "price", expression = "java(BigDecimal.ZERO)")
    abstract fun mapFrom(order: CreateOrderDto, @Context productResolver: (OrderProductDto) -> Product): Order

    protected fun resolveProduct(orderProduct: OrderProductDto, @Context productResolver: (OrderProductDto) -> Product) =
        productResolver.invoke(orderProduct)

    @AfterMapping
    protected fun afterMapping(@MappingTarget order: Order) {
        order.items.forEach { it.order = order }
    }
}