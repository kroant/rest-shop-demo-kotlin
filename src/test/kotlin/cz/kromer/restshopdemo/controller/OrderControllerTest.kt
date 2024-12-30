package cz.kromer.restshopdemo.controller

import cz.kromer.restshopdemo.SpringTest
import cz.kromer.restshopdemo.TestConstants.CASHEW_NUTS_PRODUCT_ID
import cz.kromer.restshopdemo.TestConstants.MILK_1_L_PRODUCT_ID
import cz.kromer.restshopdemo.TestConstants.MILK_500_ML_PRODUCT_ID
import cz.kromer.restshopdemo.TestConstants.SQL_CLEANUP
import cz.kromer.restshopdemo.TestConstants.SQL_COMPLEX_TEST_DATA
import cz.kromer.restshopdemo.dto.CreateOrderDto
import cz.kromer.restshopdemo.dto.OrderItemDto
import cz.kromer.restshopdemo.dto.OrderProductDto
import cz.kromer.restshopdemo.dto.OrderResponseDto
import cz.kromer.restshopdemo.dto.OrderState.CANCELLED
import cz.kromer.restshopdemo.dto.OrderState.NEW
import cz.kromer.restshopdemo.dto.OrderState.PAID
import cz.kromer.restshopdemo.service.OrderService
import cz.kromer.restshopdemo.service.ProductService
import io.restassured.http.ContentType.JSON
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit.SECONDS
import java.util.UUID

class OrderControllerTest @Autowired constructor(
    val productService: ProductService,
    val orderService: OrderService
) : SpringTest() {

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should get all orders when exist`() {
        val response = When {
            get("/orders")
        } Then {
            statusCode(OK.value())
        } Extract {
            jsonPath().getList(".", OrderResponseDto::class.java)
        }
        assertThat(response).satisfiesExactly(
            {
                assertThat(it.id).isEqualTo(UUID.fromString("27408323-1031-4658-8995-7ecff8f2b26f"))
                assertThat(it.state).isSameAs(CANCELLED)
                assertThat(it.price).isEqualByComparingTo("0")
                assertThat(it.createdOn).isEqualTo("2022-01-15T06:29:59Z")
                assertThat(it.items).isEmpty()
            }, {
                assertThat(it.id).isEqualTo(UUID.fromString("fa254654-bdbc-431b-8b9e-f6bf34540ee9"))
                assertThat(it.state).isSameAs(NEW)
                assertThat(it.price).isEqualByComparingTo("162")
                assertThat(it.createdOn).isEqualTo("2022-01-05T14:23:08Z")
                assertThat(it.items).satisfiesExactly(
                    { item ->
                        assertThat(item?.product?.id).isEqualTo(MILK_500_ML_PRODUCT_ID)
                        assertThat(item?.product?.name).isEqualTo("Milk 500 ml")
                        assertThat(item?.amount).isEqualByComparingTo("1")
                    }, { item ->
                        assertThat(item?.product?.id).isEqualTo(CASHEW_NUTS_PRODUCT_ID)
                        assertThat(item?.product?.name).isEqualTo("Cashew Nuts")
                        assertThat(item?.amount).isEqualByComparingTo("500")
                    }
                )
            }, {
                assertThat(it.id).isEqualTo(UUID.fromString("b3a48eee-65a4-431b-a11a-e770a7f0ba8b"))
                assertThat(it.state).isSameAs(NEW)
                assertThat(it.price).isEqualByComparingTo("787")
                assertThat(it.createdOn).isEqualTo("2022-01-05T16:58:46Z")
                assertThat(it.items).satisfiesExactly(
                    { item ->
                        assertThat(item?.product?.id).isEqualTo(MILK_1_L_PRODUCT_ID)
                        assertThat(item?.product?.name).isEqualTo("Milk 1 l")
                        assertThat(item?.amount).isEqualByComparingTo("2")
                    }, { item ->
                        assertThat(item?.product?.id).isEqualTo(CASHEW_NUTS_PRODUCT_ID)
                        assertThat(item?.product?.name).isEqualTo("Cashew Nuts")
                        assertThat(item?.amount).isEqualByComparingTo("2500")
                    }
                )
            }, {
                assertThat(it.id).isEqualTo(UUID.fromString("e2a878e6-72c6-49f5-b391-cb60fbca944e"))
                assertThat(it.state).isSameAs(PAID)
                assertThat(it.price).isEqualByComparingTo("120.5")
                assertThat(it.createdOn).isEqualTo("2022-01-10T09:43:00Z")
                assertThat(it.items).satisfiesExactly(
                    { item ->
                        assertThat(item?.product?.id).isEqualTo(MILK_1_L_PRODUCT_ID)
                        assertThat(item?.product?.name).isEqualTo("Milk 1 l")
                        assertThat(item?.amount).isEqualByComparingTo("1")
                    }, { item ->
                        assertThat(item?.product?.id).isEqualTo(MILK_500_ML_PRODUCT_ID)
                        assertThat(item?.product?.name).isEqualTo("Milk 500 ml")
                        assertThat(item?.amount).isEqualByComparingTo("1")
                    }, { item ->
                        assertThat(item?.product?.id).isEqualTo(CASHEW_NUTS_PRODUCT_ID)
                        assertThat(item?.product?.name).isEqualTo("Cashew Nuts")
                        assertThat(item?.amount).isEqualByComparingTo("300")
                    }
                )
            }
        )
    }

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should get order by id when exists`() {
        val orderId = UUID.fromString("fa254654-bdbc-431b-8b9e-f6bf34540ee9")
        val response = When {
            get("/orders/{id}", orderId)
        } Then {
            statusCode(OK.value())
        } Extract {
            `as`(OrderResponseDto::class.java)
        }
        assertThat(response.id).isEqualTo(orderId)
        assertThat(response.state).isSameAs(NEW)
        assertThat(response.price).isEqualByComparingTo("162")
        assertThat(response.createdOn).isEqualTo("2022-01-05T14:23:08Z")
        assertThat(response.items).satisfiesExactly(
            {
                assertThat(it?.product?.id).isEqualTo(MILK_500_ML_PRODUCT_ID)
                assertThat(it?.product?.name).isEqualTo("Milk 500 ml")
                assertThat(it?.amount).isEqualByComparingTo("1")
            }, {
                assertThat(it?.product?.id).isEqualTo(CASHEW_NUTS_PRODUCT_ID)
                assertThat(it?.product?.name).isEqualTo("Cashew Nuts")
                assertThat(it?.amount).isEqualByComparingTo("500")
            }
        )
    }

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should create order and update stock when valid`() {
        val startTime = Instant.now().truncatedTo(SECONDS)
        val response = Given {
            contentType(JSON)
            body(
                CreateOrderDto(
                    items = listOf(
                        OrderItemDto(OrderProductDto(MILK_500_ML_PRODUCT_ID), BigDecimal.valueOf(4)),
                        OrderItemDto(OrderProductDto(CASHEW_NUTS_PRODUCT_ID), BigDecimal.valueOf(750))
                    )
                )
            )
        } When {
            post("/orders")
        } Then {
            statusCode(OK.value())
        } Extract {
            `as`(OrderResponseDto::class.java)
        }
        assertThat(response.id).isNotNull()
        assertThat(response.state).isSameAs(NEW)
        assertThat(response.price).isEqualByComparingTo("273")
        assertThat(response.createdOn).isAfterOrEqualTo(startTime)
        assertThat(response.items).satisfiesExactly(
            {
                assertThat(it?.product?.id).isEqualTo(MILK_500_ML_PRODUCT_ID)
                assertThat(it?.product?.name).isEqualTo("Milk 500 ml")
                assertThat(it?.amount).isEqualByComparingTo("4")
            }, {
                assertThat(it?.product?.id).isEqualTo(CASHEW_NUTS_PRODUCT_ID)
                assertThat(it?.product?.name).isEqualTo("Cashew Nuts")
                assertThat(it?.amount).isEqualByComparingTo("750")
            }
        )
        assertThat(productService.getById(MILK_500_ML_PRODUCT_ID).stock).isEqualByComparingTo("26")
        assertThat(productService.getById(CASHEW_NUTS_PRODUCT_ID).stock).isEqualByComparingTo("99250")
    }

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should cancel order and return to stock`() {
        val orderId = UUID.fromString("fa254654-bdbc-431b-8b9e-f6bf34540ee9")
        When {
            put("/orders/{id}/cancel", orderId)
        } Then {
            statusCode(NO_CONTENT.value())
        }
        val order = orderService.getById(orderId)
        assertThat(order.id).isEqualTo(orderId)
        assertThat(order.state).isSameAs(CANCELLED)
        assertThat(order.price).isEqualByComparingTo("162")

        assertThat(productService.getById(MILK_500_ML_PRODUCT_ID).stock).isEqualByComparingTo("31")
        assertThat(productService.getById(CASHEW_NUTS_PRODUCT_ID).stock).isEqualByComparingTo("100500")
    }

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should pay order`() {
        val orderId = UUID.fromString("fa254654-bdbc-431b-8b9e-f6bf34540ee9")
        When {
            put("/orders/{id}/pay", orderId)
        } Then {
            statusCode(NO_CONTENT.value())
        }
        val order = orderService.getById(orderId)
        assertThat(order.id).isEqualTo(orderId)
        assertThat(order.state).isSameAs(PAID)
        assertThat(order.price).isEqualByComparingTo("162")
    }
}