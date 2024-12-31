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
import cz.kromer.restshopdemo.dto.OrderState.NEW
import cz.kromer.restshopdemo.dto.OrderState.PAID
import cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.ALLOWED_STATE
import cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.CURRENT_STATE
import cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.MISSING_AMOUNT
import cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.VALIDATION_CODE
import cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ENTITY_NOT_FOUND
import cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ILLEGAL_AMOUNT_SCALE
import cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ILLEGAL_ORDER_STATE
import cz.kromer.restshopdemo.dto.error.ErrorResponseCode.PRODUCT_STOCK_SHORTAGE
import cz.kromer.restshopdemo.dto.error.ErrorResponseCode.REQUEST_VALIDATION_ERROR
import cz.kromer.restshopdemo.dto.error.ErrorResponseDto
import cz.kromer.restshopdemo.dto.validation.UniqueOrderProduct
import cz.kromer.restshopdemo.service.OrderService
import cz.kromer.restshopdemo.service.ProductService
import io.restassured.http.ContentType.JSON
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import jakarta.validation.constraints.NotNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import java.util.UUID

class OrderControllerValidationTest @Autowired constructor(
    val productService: ProductService,
    val orderService: OrderService
) : SpringTest() {

    @Test
    fun `should fail 400 when order item null`() {
        val response = Given {
            contentType(JSON)
            body(
                CreateOrderDto(
                    items = listOf(null, OrderItemDto(OrderProductDto(CASHEW_NUTS_PRODUCT_ID), BigDecimal.valueOf(110000)))
                )
            )
        } When {
            post("/orders")
        } Then {
            statusCode(BAD_REQUEST.value())
        } Extract {
            `as`(ErrorResponseDto::class.java)
        }
        assertThat(response.errorCode).isSameAs(REQUEST_VALIDATION_ERROR)
        assertThat(response.errorDetails).satisfiesExactly({
            assertThat(it.entityId).isNull()
            assertThat(it.field).isEqualTo("items[0]")
            assertThat(it.message).isEqualTo("must not be null")
            assertThat(it.values).satisfiesExactly({ detailValue ->
                assertThat(detailValue.type).isSameAs(VALIDATION_CODE)
                assertThat(detailValue.value).isEqualTo(NotNull::class.simpleName)
            })
        })
    }

    @Test
    fun `should fail 400 when product id null`() {
        val response = Given {
            contentType(JSON)
            body(
                CreateOrderDto(
                    items = listOf(
                        OrderItemDto(OrderProductDto(null), TEN),
                        OrderItemDto(OrderProductDto(CASHEW_NUTS_PRODUCT_ID), BigDecimal.valueOf(110000))
                    )
                )
            )
        } When {
            post("/orders")
        } Then {
            statusCode(BAD_REQUEST.value())
        } Extract {
            `as`(ErrorResponseDto::class.java)
        }
        assertThat(response.errorCode).isSameAs(REQUEST_VALIDATION_ERROR)
        assertThat(response.errorDetails).satisfiesExactly({
            assertThat(it.entityId).isNull()
            assertThat(it.field).isEqualTo("items[0].product.id")
            assertThat(it.message).isEqualTo("must not be null")
            assertThat(it.values).satisfiesExactly({ detailValue ->
                assertThat(detailValue.type).isSameAs(VALIDATION_CODE)
                assertThat(detailValue.value).isEqualTo(NotNull::class.simpleName)
            })
        })
    }

    @Test
    fun `should fail 400 when product id duplicate`() {
        val response = Given {
            contentType(JSON)
            body(
                CreateOrderDto(
                    items = listOf(
                        OrderItemDto(OrderProductDto(MILK_1_L_PRODUCT_ID), TEN),
                        OrderItemDto(OrderProductDto(MILK_1_L_PRODUCT_ID), ONE))
                )
            )
        } When {
            post("/orders")
        } Then {
            statusCode(BAD_REQUEST.value())
        } Extract {
            `as`(ErrorResponseDto::class.java)
        }
        assertThat(response.errorCode).isSameAs(REQUEST_VALIDATION_ERROR)
        assertThat(response.errorDetails).satisfiesExactly({
            assertThat(it.entityId).isNull()
            assertThat(it.field).isEqualTo("items")
            assertThat(it.message).isEqualTo("product must be unique")
            assertThat(it.values).satisfiesExactly({ detailValue ->
                assertThat(detailValue.type).isSameAs(VALIDATION_CODE)
                assertThat(detailValue.value).isEqualTo(UniqueOrderProduct::class.simpleName)
            })
        })
    }

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should fail 400 and leave stock unchanged when illegal amount scale`() {
        val response = Given {
            contentType(JSON)
            body(
                CreateOrderDto(
                    items = listOf(
                        OrderItemDto(OrderProductDto(MILK_500_ML_PRODUCT_ID), BigDecimal.valueOf(4)),
                        OrderItemDto(OrderProductDto(CASHEW_NUTS_PRODUCT_ID), BigDecimal.valueOf(10005, 1))
                    )
                )
            )
        } When {
            post("/orders")
        } Then {
            statusCode(BAD_REQUEST.value())
        } Extract {
            `as`(ErrorResponseDto::class.java)
        }
        assertThat(response.errorCode).isSameAs(ILLEGAL_AMOUNT_SCALE)
        assertThat(response.errorDetails).satisfiesExactly({
            assertThat(it.entityId).isEqualTo(CASHEW_NUTS_PRODUCT_ID)
            assertThat(it.field).isNull()
            assertThat(it.message).isNull()
            assertThat(it.values).isEmpty()
        })
        assertThat(productService.getById(MILK_500_ML_PRODUCT_ID).stock).isEqualByComparingTo("30")
        assertThat(productService.getById(CASHEW_NUTS_PRODUCT_ID).stock).isEqualByComparingTo("100000")
    }

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should fail 400 and leave stock unchanged when product stock shortage`() {
        val response = Given {
            contentType(JSON)
            body(
                CreateOrderDto(
                    items = listOf(
                        OrderItemDto(OrderProductDto(MILK_500_ML_PRODUCT_ID), BigDecimal.valueOf(32)),
                        OrderItemDto(OrderProductDto(CASHEW_NUTS_PRODUCT_ID), BigDecimal.valueOf(110000))
                    )
                )
            )
        } When {
            post("/orders")
        } Then {
            statusCode(BAD_REQUEST.value())
        } Extract {
            `as`(ErrorResponseDto::class.java)
        }
        assertThat(response.errorCode).isSameAs(PRODUCT_STOCK_SHORTAGE)
        assertThat(response.errorDetails).satisfiesExactly(
            {
                assertThat(it.entityId).isEqualTo(MILK_500_ML_PRODUCT_ID)
                assertThat(it.field).isNull()
                assertThat(it.message).isNull()
                assertThat(it.values).satisfiesExactly({ value ->
                    assertThat(value.type).isSameAs(MISSING_AMOUNT)
                    assertThat(value.value).isEqualTo("2.000")
                })
            }, {
                assertThat(it.entityId).isEqualTo(CASHEW_NUTS_PRODUCT_ID)
                assertThat(it.field).isNull()
                assertThat(it.message).isNull()
                assertThat(it.values).satisfiesExactly({ value ->
                    assertThat(value.type).isSameAs(MISSING_AMOUNT)
                    assertThat(value.value).isEqualTo("10000.000")
                })
            }
        )
        assertThat(productService.getById(MILK_500_ML_PRODUCT_ID).stock).isEqualByComparingTo("30")
        assertThat(productService.getById(CASHEW_NUTS_PRODUCT_ID).stock).isEqualByComparingTo("100000")
    }

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should fail 400 and leave stock unchanged when product not found`() {
        val productId = UUID.fromString("c51933b3-60bb-4b50-a29b-ad71f962095a")
        val response = Given {
            contentType(JSON)
            body(
                CreateOrderDto(
                    items = listOf(
                        OrderItemDto(OrderProductDto(MILK_500_ML_PRODUCT_ID), BigDecimal.valueOf(4)),
                        OrderItemDto(OrderProductDto(productId), BigDecimal.valueOf(1000))
                    )
                )
            )
        } When {
            post("/orders")
        } Then {
            statusCode(BAD_REQUEST.value())
        } Extract {
            `as`(ErrorResponseDto::class.java)
        }
        assertThat(response.errorCode).isSameAs(ENTITY_NOT_FOUND)
        assertThat(response.errorDetails).satisfiesExactly({
            assertThat(it.entityId).isEqualTo(productId)
            assertThat(it.field).isNull()
            assertThat(it.message).isNull()
            assertThat(it.values).isEmpty()
        })
        assertThat(productService.getById(MILK_500_ML_PRODUCT_ID).stock).isEqualByComparingTo("30")
    }

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should fail 400 and not cancel when illegal order state`() {
        val paidOrderId = UUID.fromString("e2a878e6-72c6-49f5-b391-cb60fbca944e")
        val response = When {
            put("/orders/{id}/cancel", paidOrderId)
        } Then {
            statusCode(BAD_REQUEST.value())
        } Extract {
            `as`(ErrorResponseDto::class.java)
        }
        assertThat(response.errorCode).isSameAs(ILLEGAL_ORDER_STATE)
        assertThat(response.errorDetails).satisfiesExactly({
            assertThat(it.entityId).isEqualTo(paidOrderId)
            assertThat(it.field).isNull()
            assertThat(it.message).isNull()
            assertThat(it.values).satisfiesExactly(
                { value ->
                    assertThat(value.type).isSameAs(CURRENT_STATE)
                    assertThat(value.value).isEqualTo(PAID.name)
                }, { value ->
                    assertThat(value.type).isSameAs(ALLOWED_STATE)
                    assertThat(value.value).isEqualTo(NEW.name)
                }
            )
        })
        val order = orderService.getById(paidOrderId)
        assertThat(order.id).isEqualTo(paidOrderId)
        assertThat(order.state).isSameAs(PAID)
    }
}