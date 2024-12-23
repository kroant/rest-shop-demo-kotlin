package cz.kromer.restshopdemo.mapper

import cz.kromer.restshopdemo.dto.ProductDto
import cz.kromer.restshopdemo.entity.Product
import org.mapstruct.Mapper

@Mapper
interface ProductMapper {

    fun mapToProductDto(product: Product): ProductDto

    fun mapToProduct(product: ProductDto): Product
}