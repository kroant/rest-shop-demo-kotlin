package cz.kromer.restshopdemo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.scheduling")
data class SchedulingProps(
    val orderCancellation: OrderCancellationProps = OrderCancellationProps(),
)