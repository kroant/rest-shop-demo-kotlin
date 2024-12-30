package cz.kromer.restshopdemo.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.math.BigDecimal

@Entity
class OrderItem(

    @Id
    @ManyToOne
    var order: Order?,

    @Id
    @ManyToOne
    val product: Product,

    val amount: BigDecimal
) {

    override fun equals(other: Any?) =
        this === other
                || other is OrderItem
                && product.id == other.product.id
                && order?.id == other.order?.id

    override fun hashCode(): Int {
        var result = order?.id.hashCode()
        result = 31 * result + product.id.hashCode()
        return result
    }
}