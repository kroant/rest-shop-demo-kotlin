package cz.kromer.restshopdemo.service

import cz.kromer.restshopdemo.config.SchedulingProps
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory.getLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant.now
import java.time.temporal.ChronoUnit.SECONDS
import java.util.UUID

@Component
class OrderCancellationTask(
    private val clock: Clock,
    private val orderService: OrderService,
    private val schedulingProps: SchedulingProps,
) {

    private val log = getLogger(javaClass)

    @Scheduled(cron = "\${app.scheduling.order-cancellation.cron}")
    @SchedulerLock(name = "cancelObsoleteOrders")
    fun cancelObsoleteOrders() {
        val before = (now(clock) - schedulingProps.orderCancellation.newOrderRetentionDuration).truncatedTo(SECONDS)
        log.info("Cancelling NEW orders created before {}", before)
        orderService.findNewOrdersBefore(before).forEach(::cancelOrder)
    }

    private fun cancelOrder(id: UUID) {
        log.info("Cancelling order {}", id)
        try {
            orderService.cancel(id)
        } catch (exception: RuntimeException) {
            log.error("Exception during order cancelling: $id", exception)
        }
    }
}