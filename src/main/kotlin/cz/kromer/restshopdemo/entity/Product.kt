package cz.kromer.restshopdemo.entity

import cz.kromer.restshopdemo.dto.QuantityUnit
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.hibernate.annotations.SQLRestriction
import java.math.BigDecimal
import java.util.UUID

@Entity
@SQLRestriction("deleted = false")
class Product(

    @Id
    @GeneratedValue
    val id: UUID?,

    var name: String,

    @Enumerated(STRING)
    var unit: QuantityUnit,

    var price: BigDecimal,

    var stock: BigDecimal,

    var deleted: Boolean
)
