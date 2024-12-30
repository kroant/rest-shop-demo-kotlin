package cz.kromer.restshopdemo.controller

import cz.kromer.restshopdemo.SpringTest
import cz.kromer.restshopdemo.TestConstants.CASHEW_NUTS_PRODUCT_ID
import cz.kromer.restshopdemo.TestConstants.MILK_1_L_PRODUCT_ID
import cz.kromer.restshopdemo.TestConstants.MILK_500_ML_PRODUCT_ID
import cz.kromer.restshopdemo.TestConstants.SQL_CLEANUP
import cz.kromer.restshopdemo.TestConstants.SQL_COMPLEX_TEST_DATA
import cz.kromer.restshopdemo.dto.ProductDto
import cz.kromer.restshopdemo.dto.QuantityUnit.GRAM
import cz.kromer.restshopdemo.dto.QuantityUnit.LITER
import cz.kromer.restshopdemo.dto.QuantityUnit.PIECE
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
import java.util.UUID

class ProductControllerTest @Autowired constructor(
    val productService: ProductService
) : SpringTest() {

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should get all products when exist`() {
        val response = When {
            get("/products")
        } Then {
            statusCode(OK.value())
        } Extract {
            jsonPath().getList(".", ProductDto::class.java)
        }
        assertThat(response).satisfiesExactly(
            {
                assertThat(it.id).isEqualTo(MILK_1_L_PRODUCT_ID)
                assertThat(it.name).isEqualTo("Milk 1 l")
                assertThat(it.unit).isSameAs(PIECE)
                assertThat(it.price).isEqualByComparingTo("18.5")
                assertThat(it.stock).isEqualByComparingTo("50")
            }, {
                assertThat(it.id).isEqualTo(MILK_500_ML_PRODUCT_ID)
                assertThat(it.name).isEqualTo("Milk 500 ml")
                assertThat(it.unit).isSameAs(PIECE)
                assertThat(it.price).isEqualByComparingTo("12")
                assertThat(it.stock).isEqualByComparingTo("30")
            }, {
                assertThat(it.id).isEqualTo(CASHEW_NUTS_PRODUCT_ID)
                assertThat(it.name).isEqualTo("Cashew Nuts")
                assertThat(it.unit).isSameAs(GRAM)
                assertThat(it.price).isEqualByComparingTo("0.3")
                assertThat(it.stock).isEqualByComparingTo("100000")
            }
        )
    }

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should get product by id when exists`() {
        val response = When {
            get("/products/{id}", MILK_500_ML_PRODUCT_ID)
        } Then {
            statusCode(OK.value())
        } Extract {
            `as`(ProductDto::class.java)
        }
        assertThat(response.id).isEqualTo(MILK_500_ML_PRODUCT_ID)
        assertThat(response.name).isEqualTo("Milk 500 ml")
        assertThat(response.unit).isSameAs(PIECE)
        assertThat(response.price).isEqualByComparingTo("12")
        assertThat(response.stock).isEqualByComparingTo("30")
    }

    @Test
    fun `should save product when valid`() {
        val response = Given {
            contentType(JSON)
            body(
                ProductDto(
                    name = "Bread 1 kg",
                    unit = PIECE,
                    price = BigDecimal.valueOf(32),
                    stock = BigDecimal.valueOf(6000, 2)
                )
            )
        } When {
            post("/products")
        } Then {
            statusCode(OK.value())
        } Extract {
            `as`(ProductDto::class.java)
        }
        assertThat(response.id).isNotNull()
        assertThat(response.name).isEqualTo("Bread 1 kg")
        assertThat(response.unit).isSameAs(PIECE)
        assertThat(response.price).isEqualByComparingTo("32")
        assertThat(response.stock).isEqualByComparingTo("60")
    }

    @Test
    fun `should generate id and save product when id provided`() {
        val myUuid = UUID.fromString("dbff76c6-f59a-493b-95ee-98a4eef99a78")
        val response = Given {
            contentType(JSON)
            body(
                ProductDto(
                    id = myUuid,
                    name = "Bread 1 kg",
                    unit = PIECE,
                    price = BigDecimal.valueOf(32),
                    stock = BigDecimal.valueOf(6000, 2)
                )
            )
        } When {
            post("/products")
        } Then {
            statusCode(OK.value())
        } Extract {
            `as`(ProductDto::class.java)
        }
        assertThat(response.id).isNotNull().isNotEqualTo(myUuid)
        assertThat(response.name).isEqualTo("Bread 1 kg")
        assertThat(response.unit).isSameAs(PIECE)
        assertThat(response.price).isEqualByComparingTo("32")
        assertThat(response.stock).isEqualByComparingTo("60")
    }

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should update product when valid and exists`() {
        val response = Given {
            contentType(JSON)
            body(
                ProductDto(
                    name = "Updated Milk 500 ml",
                    unit = LITER,
                    price = BigDecimal.valueOf(10),
                    stock = BigDecimal.valueOf(40)
                )
            )
        } When {
            put("/products/{id}", MILK_500_ML_PRODUCT_ID)
        } Then {
            statusCode(OK.value())
        } Extract {
            `as`(ProductDto::class.java)
        }
        assertThat(response.id).isEqualTo(MILK_500_ML_PRODUCT_ID)
        assertThat(response.name).isEqualTo("Updated Milk 500 ml")
        assertThat(response.unit).isSameAs(LITER)
        assertThat(response.price).isEqualByComparingTo("10")
        assertThat(response.stock).isEqualByComparingTo("40")

        val updated = productService.getById(MILK_500_ML_PRODUCT_ID)
        assertThat(updated.id).isEqualTo(MILK_500_ML_PRODUCT_ID)
        assertThat(updated.name).isEqualTo("Updated Milk 500 ml")
        assertThat(updated.unit).isSameAs(LITER)
        assertThat(updated.price).isEqualByComparingTo("10")
        assertThat(updated.stock).isEqualByComparingTo("40")
    }

    @Test
    @Sql(SQL_CLEANUP, SQL_COMPLEX_TEST_DATA)
    fun `should delete product when exists`() {
        When {
            delete("/products/{id}", MILK_500_ML_PRODUCT_ID)
        } Then {
            statusCode(NO_CONTENT.value())
        }
        assertThat(productService.findAll()).satisfiesExactly(
            { assertThat(it.id).isEqualTo(MILK_1_L_PRODUCT_ID) },
            { assertThat(it.id).isEqualTo(CASHEW_NUTS_PRODUCT_ID) },
        )
    }
}