package cz.kromer.restshopdemo.mapper

import cz.kromer.restshopdemo.dto.OrderProductDto
import cz.kromer.restshopdemo.entity.Product
import org.mapstruct.Mapper

@Mapper
interface OrderProductDtoMapper {

    fun mapFrom(product: Product): OrderProductDto
}