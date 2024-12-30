package cz.kromer.restshopdemo.mapper

import cz.kromer.restshopdemo.dto.ProductDto
import cz.kromer.restshopdemo.entity.Product
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget

@Mapper
interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    fun mapFrom(product: ProductDto): Product

    @Mapping(target = "id", ignore = true)
    fun mapToProduct(source: ProductDto, @MappingTarget entity: Product)
}