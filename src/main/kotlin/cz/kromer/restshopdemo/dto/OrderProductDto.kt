package cz.kromer.restshopdemo.dto

import jakarta.validation.constraints.NotNull
import java.util.UUID

data class OrderProductDto(

    @field:NotNull
    val id: UUID?,

    val name: String? = null
)