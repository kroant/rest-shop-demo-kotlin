package cz.kromer.restshopdemo.service

import cz.kromer.restshopdemo.SpringTest
import cz.kromer.restshopdemo.TestConstants.MILK_1_L_PRODUCT_ID
import cz.kromer.restshopdemo.TestConstants.MILK_500_ML_PRODUCT_ID
import cz.kromer.restshopdemo.TestConstants.SQL_CLEANUP
import cz.kromer.restshopdemo.TestConstants.SQL_COMPLEX_TEST_DATA
import cz.kromer.restshopdemo.dto.CreateOrderDto
import cz.kromer.restshopdemo.dto.OrderItemDto
import cz.kromer.restshopdemo.dto.OrderProductDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.task.TaskExecutor
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal.ONE

@Disabled("Locking Test uses thread sleeping. Only for locking investigation purpose.")
class ProductLockingTest @Autowired constructor(
    private val productService: ProductService,
    private val orderService: OrderService,
    private val lockingTransactionFactory: ObjectFactory<ProductLockingTransaction>,
    private val taskExecutor: TaskExecutor,
) : SpringTest() {

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should lock product for concurrent order`() {
        val lockingTransaction = lockingTransactionFactory.getObject()

        taskExecutor.execute { lockingTransaction.lockAndSleep(MILK_500_ML_PRODUCT_ID, 2200) }

        lockingTransaction.waitUntilProductLocked()

        orderService.create(
            CreateOrderDto(
                items = listOf(
                    OrderItemDto(
                        product = OrderProductDto(MILK_1_L_PRODUCT_ID),
                        amount = ONE
                    ),
                    OrderItemDto(
                        product = OrderProductDto(MILK_500_ML_PRODUCT_ID),
                        amount = ONE
                    )
                )
            )
        )

        assertThat(productService.getById(MILK_1_L_PRODUCT_ID).stock).isEqualByComparingTo("49")
        assertThat(productService.getById(MILK_500_ML_PRODUCT_ID).stock).isEqualByComparingTo("29")
    }
}