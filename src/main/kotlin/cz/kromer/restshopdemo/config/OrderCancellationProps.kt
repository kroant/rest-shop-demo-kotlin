package cz.kromer.restshopdemo.config

import java.time.Duration
import java.time.Duration.ofMinutes

data class OrderCancellationProps(
    var newOrderRetentionDuration: Duration = ofMinutes(30)
)
