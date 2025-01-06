package cz.kromer.restshopdemo

import io.restassured.RestAssured
import io.restassured.config.LogConfig.logConfig
import io.restassured.filter.log.LogDetail.ALL
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Clock

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class SpringTest {

    @MockitoBean
    protected lateinit var clock: Clock

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    protected fun setup() {
        RestAssured.port = port

        RestAssured.config = RestAssured.config()
            .logConfig(
                logConfig()
                    .enableLoggingOfRequestAndResponseIfValidationFails(ALL)
                    .enablePrettyPrinting(true)
            )
        RestAssured.replaceFiltersWith(RequestLoggingFilter(), ResponseLoggingFilter())
    }
}