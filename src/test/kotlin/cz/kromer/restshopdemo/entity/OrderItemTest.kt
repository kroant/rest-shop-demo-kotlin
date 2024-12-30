package cz.kromer.restshopdemo.entity

import cz.kromer.restshopdemo.dto.OrderState.NEW
import cz.kromer.restshopdemo.dto.QuantityUnit.PIECE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import java.math.BigDecimal.ZERO
import java.util.UUID

class OrderItemTest {

    @Test
    fun `should be equal when order is null or order id is null`() {

        val item1 = OrderItem(
            product = Product(
                id = UUID.fromString("314aeb75-06ab-433a-b20d-3ddfd01d659e"),
                name = "Bread",
                unit = PIECE,
                price = TEN,
                stock = ZERO,
                deleted = false
            ),
            order = null,
            amount = ONE
        )

        val item2 = OrderItem(
            product = Product(
                id = UUID.fromString("314aeb75-06ab-433a-b20d-3ddfd01d659e"),
                name = "Bread",
                unit = PIECE,
                price = TEN,
                stock = ZERO,
                deleted = false
            ),
            order = Order(
                id = null,
                price = ONE,
                state = NEW,
                items = emptyList(),
                createdOn = null
            ),
            amount = ONE
        )

        assertThat(item1 == item2).isTrue()
    }

    @Test
    fun `should return zero hash code when order is null and product id is null`() {

        val item = OrderItem(
            product = Product(
                id = null,
                name = "Bread",
                unit = PIECE,
                price = TEN,
                stock = ZERO,
                deleted = false
            ),
            order = null,
            amount = ONE
        )

        assertThat(item.hashCode()).isEqualTo(0)
    }
}