package cz.kromer.restshopdemo.exception

import java.util.UUID

class IllegalAmountScaleException (
    val productId: UUID
) : RuntimeException("Illegal amount scale for product: $productId")