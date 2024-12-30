package cz.kromer.restshopdemo.exception

class ProductShortageException(
    val productShortages: List<ProductStockShortageDto>
) : RuntimeException()