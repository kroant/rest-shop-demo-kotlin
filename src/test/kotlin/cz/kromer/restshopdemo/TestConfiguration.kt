package cz.kromer.restshopdemo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor

@Configuration
class TestConfiguration {

    @Bean
    fun taskExecutor() = SimpleAsyncTaskExecutor()
}