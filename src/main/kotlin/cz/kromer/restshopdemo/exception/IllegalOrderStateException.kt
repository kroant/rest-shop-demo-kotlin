package cz.kromer.restshopdemo.exception

import cz.kromer.restshopdemo.dto.OrderState
import java.util.EnumSet
import java.util.UUID

class IllegalOrderStateException(
    val id: UUID,
    val currentState: OrderState,
    val allowedStates: EnumSet<OrderState>
) : RuntimeException("Illegal Order state. Order ID: $id, state: $currentState")