package cz.kromer.restshopdemo.dto

import cz.kromer.restshopdemo.dto.validation.ProductStockMaxScale
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.UUID

@ProductStockMaxScale
data class ProductDto(

    val id: UUID? = null,

    @field:NotBlank
    // Single line text
    @field:Pattern(regexp = "^[\\p{L}\\p{S}\\p{N}\\p{P}\\x20]*$")
    @field:Size(max = 100)
    val name: String?,

    @field:NotNull
    val unit: QuantityUnit?,

    @field:NotNull
    @field:Positive
    @field:Digits(integer = 17, fraction = 2)
    val price: BigDecimal?,

    @field:NotNull
    @field:Positive
    @field:Digits(integer = 16, fraction = 3)
    val stock: BigDecimal?
)