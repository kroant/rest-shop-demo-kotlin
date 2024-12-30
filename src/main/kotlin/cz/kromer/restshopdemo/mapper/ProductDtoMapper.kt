package cz.kromer.restshopdemo.mapper

import cz.kromer.restshopdemo.dto.ProductDto
import cz.kromer.restshopdemo.entity.Product
import org.mapstruct.Mapper

@Mapper
interface ProductDtoMapper {

    fun mapFrom(product: Product): ProductDto
}