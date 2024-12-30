package cz.kromer.restshopdemo.service

import cz.kromer.restshopdemo.config.SchedulingProps
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.then
import java.time.Clock
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class OrderCancellationTaskExceptionHandlingTest {

    @Mock
    private lateinit var orderService: OrderService

    @Spy
    private val clock = Clock.systemDefaultZone()

    @Spy
    private val schedulingProps = SchedulingProps()

    @InjectMocks
    private lateinit var orderCancellationTask: OrderCancellationTask

    @Test
    fun `should process second order when first order fails`() {
        val firstOrderId = UUID.fromString("daa9498b-c136-4fe1-8684-721ef41c15d1")
        val secondOrderId = UUID.fromString("104c5d5e-f4fc-49e1-92b4-14b9a50e7110")

        given(orderService.findNewOrdersBefore(any()))
            .willReturn(listOf(firstOrderId, secondOrderId))

        given(orderService.cancel(firstOrderId))
            .willThrow(RuntimeException("Exception during cancelling an order"))

        orderCancellationTask.cancelObsoleteOrders()

        then(orderService)
            .should()
            .cancel(firstOrderId)

        then(orderService)
            .should()
            .cancel(secondOrderId)
    }
}