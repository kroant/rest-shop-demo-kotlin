package cz.kromer.restshopdemo.mapper

import cz.kromer.restshopdemo.dto.OrderResponseDto
import cz.kromer.restshopdemo.entity.Order
import org.mapstruct.Mapper

@Mapper(uses = [OrderProductDtoMapper::class])
interface OrderResponseDtoMapper {

    fun mapFrom(order: Order): OrderResponseDto
}