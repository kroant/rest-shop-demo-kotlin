package cz.kromer.restshopdemo.controller

import cz.kromer.restshopdemo.SpringTest
import cz.kromer.restshopdemo.dto.ProductDto
import cz.kromer.restshopdemo.dto.QuantityUnit.PIECE
import cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.VALIDATION_CODE
import cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ENTITY_NOT_FOUND
import cz.kromer.restshopdemo.dto.error.ErrorResponseCode.REQUEST_VALIDATION_ERROR
import cz.kromer.restshopdemo.dto.error.ErrorResponseDto
import io.restassured.http.ContentType.JSON
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import java.math.BigDecimal
import java.math.BigDecimal.TEN
import java.util.UUID

class ProductControllerValidationTest : SpringTest() {

    @Test
    fun `should fail 400 when id invalid`() {
        When {
            get("/products/invalid_UUID")
        } Then {
            statusCode(BAD_REQUEST.value())
        }
    }

    @Test
    fun `should fail 404 when not found`() {
        val productId = UUID.fromString("ea329635-7fae-48ab-816d-3b2255590311")
        val response = When {
            get("/products/{id}", productId)
        } Then {
            statusCode(NOT_FOUND.value())
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
    }

    @Test
    fun `should fail 400 when values in fields invalid`() {
        val response = Given {
            contentType(JSON)
            body(
                ProductDto(
                    name = "\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n" +
                            "\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t",
                    unit = null,
                    price = BigDecimal.valueOf(-32),
                    stock = BigDecimal.valueOf(1, 4)
                )
            )
        } When {
            post("/products")
        } Then {
            statusCode(BAD_REQUEST.value())
        } Extract {
            `as`(ErrorResponseDto::class.java)
        }
        assertThat(response.errorCode).isSameAs(REQUEST_VALIDATION_ERROR)
        assertThat(response.errorDetails).allSatisfy {
            assertThat(it.entityId).isNull()
            assertThat(it.values).satisfiesExactly({ detailValue -> assertThat(detailValue.type).isSameAs(VALIDATION_CODE) })
        }
        assertThat(response.errorDetails).satisfiesExactlyInAnyOrder(
            {
                assertThat(it.field).isEqualTo("name")
                assertThat(it.message).startsWith("must match")
                assertThat(it.values).satisfiesExactly({ detailValue -> assertThat(detailValue.value).isEqualTo("Pattern") })
            }, {
                assertThat(it.field).isEqualTo("name")
                assertThat(it.message).isEqualTo("must not be blank")
                assertThat(it.values).satisfiesExactly({ detailValue -> assertThat(detailValue.value).isEqualTo("NotBlank") })
            }, {
                assertThat(it.field).isEqualTo("name")
                assertThat(it.message).startsWith("size must be between 0 and")
                assertThat(it.values).satisfiesExactly({ detailValue -> assertThat(detailValue.value).isEqualTo("Size") })
            }, {
                assertThat(it.field).isEqualTo("unit")
                assertThat(it.message).isEqualTo("must not be null")
                assertThat(it.values).satisfiesExactly({ detailValue -> assertThat(detailValue.value).isEqualTo("NotNull") })
            }, {
                assertThat(it.field).isEqualTo("price")
                assertThat(it.message).isEqualTo("must be greater than 0")
                assertThat(it.values).satisfiesExactly({ detailValue -> assertThat(detailValue.value).isEqualTo("Positive") })
            }, {
                assertThat(it.field).isEqualTo("stock")
                assertThat(it.message).startsWith("numeric value out of bounds")
                assertThat(it.values).satisfiesExactly({ detailValue -> assertThat(detailValue.value).isEqualTo("Digits") })
            }
        )
    }

    @Test
    fun `should fail 400 when piece scale invalid`() {
        val response = Given {
            contentType(JSON)
            body(
                ProductDto(
                    name = "Invalid scale",
                    unit = PIECE,
                    price = TEN,
                    stock = BigDecimal.valueOf(105, 1)
                )
            )
        } When {
            post("/products")
        } Then {
            statusCode(BAD_REQUEST.value())
        } Extract {
            `as`(ErrorResponseDto::class.java)
        }
        assertThat(response.errorCode).isSameAs(REQUEST_VALIDATION_ERROR)
        assertThat(response.errorDetails).satisfiesExactly({
            assertThat(it.entityId).isNull()
            assertThat(it.field).isNull()
            assertThat(it.message).isEqualTo("product stock scale must be less or equal to unit max scale")
            assertThat(it.values).satisfiesExactly({ detailValue ->
                assertThat(detailValue.type).isSameAs(VALIDATION_CODE)
                assertThat(detailValue.value).isEqualTo("ProductStockMaxScale")
            })
        })
    }
}