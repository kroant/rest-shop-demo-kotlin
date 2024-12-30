package cz.kromer.restshopdemo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry
import java.time.Clock

@Configuration
@EnableRetry
class AppConfig {

    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()
}