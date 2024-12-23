package cz.kromer.restshopdemo.entity

import cz.kromer.restshopdemo.dto.QuantityUnit
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.math.BigDecimal
import java.util.UUID

@Entity
data class Product(

    @Id
    @GeneratedValue
    val id: UUID,

    val name: String,

    @Enumerated(STRING)
    val unit: QuantityUnit,

    val price: BigDecimal,

    val stock: BigDecimal,

    val deleted: Boolean
)
