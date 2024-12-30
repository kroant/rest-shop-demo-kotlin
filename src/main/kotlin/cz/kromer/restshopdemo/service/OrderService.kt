package cz.kromer.restshopdemo.service

import cz.kromer.restshopdemo.dto.CreateOrderDto
import cz.kromer.restshopdemo.dto.OrderProductDto
import cz.kromer.restshopdemo.dto.OrderState
import cz.kromer.restshopdemo.dto.OrderState.CANCELLED
import cz.kromer.restshopdemo.dto.OrderState.NEW
import cz.kromer.restshopdemo.dto.OrderState.PAID
import cz.kromer.restshopdemo.entity.Order
import cz.kromer.restshopdemo.entity.OrderItem
import cz.kromer.restshopdemo.entity.Product
import cz.kromer.restshopdemo.exception.AssociatedEntityNotFoundException
import cz.kromer.restshopdemo.exception.IllegalAmountScaleException
import cz.kromer.restshopdemo.exception.IllegalOrderStateException
import cz.kromer.restshopdemo.exception.RootEntityNotFoundException
import cz.kromer.restshopdemo.mapper.OrderMapper
import cz.kromer.restshopdemo.mapper.OrderResponseDtoMapper
import cz.kromer.restshopdemo.repository.OrderRepository
import cz.kromer.restshopdemo.repository.ProductRepository
import cz.kromer.restshopdemo.utils.isScaleValid
import org.springframework.beans.factory.ObjectFactory
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation.READ_COMMITTED
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.EnumSet
import java.util.UUID

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val orderMapper: OrderMapper,
    private val orderResponseDtoMapper: OrderResponseDtoMapper,
    private val stockShortageWatcherFactory: ObjectFactory<StockShortageWatcher>
) {

    @Transactional(readOnly = true)
    fun findAll() = orderRepository.findAll().map { orderResponseDtoMapper.mapFrom(it) }

    @Transactional(readOnly = true)
    fun getById(id: UUID) = orderRepository.findByIdOrNull(id)
        ?.let { orderResponseDtoMapper.mapFrom(it) }
        ?: throw RootEntityNotFoundException(id)

    @Retryable(retryFor = [ConcurrencyFailureException::class])
    @Transactional(isolation = READ_COMMITTED)
    fun create(order: CreateOrderDto): UUID {
        var entity = orderMapper.mapFrom(order, ::findPersistentProductAndLock)
        val items = entity.items

        items.forEach(::validateAmountScale)

        val stock = stockShortageWatcherFactory.getObject()
        stock.take(items)

        entity.price = items.sumOf { it.countPrice() }

        entity = orderRepository.save(entity)
        return checkNotNull(entity.id)
    }

    @Retryable(retryFor = [ConcurrencyFailureException::class])
    @Transactional(isolation = READ_COMMITTED)
    fun cancel(id: UUID) {
        val order = orderRepository.findAndLockById(id) ?: throw RootEntityNotFoundException(id)
        order.validateState(NEW)

        order.items.forEach(::returnToStock)
        order.state = CANCELLED
    }

    @Retryable(retryFor = [ConcurrencyFailureException::class])
    @Transactional(isolation = READ_COMMITTED)
    fun pay(id: UUID) {
        val order = orderRepository.findAndLockById(id) ?: throw RootEntityNotFoundException(id)
        order.validateState(NEW)

        order.state = PAID
    }

    @Transactional(readOnly = true)
    fun findNewOrdersBefore(before: Instant) = orderRepository.findNewOrdersBefore(before)

    private fun findPersistentProductAndLock(orderProduct: OrderProductDto): Product {
        val id = checkNotNull(orderProduct.id)
        return productRepository.findAndLockById(id) ?: throw AssociatedEntityNotFoundException(id)
    }

    private fun returnToStock(orderItem: OrderItem) =
        productRepository.findAndLockById(checkNotNull(orderItem.product.id))
            ?.let { it.stock += orderItem.amount }

    private fun validateAmountScale(orderItem: OrderItem) {
        val product = orderItem.product
        if (!product.unit.isScaleValid(orderItem.amount)) {
            throw IllegalAmountScaleException(checkNotNull(product.id))
        }
    }

    private fun OrderItem.countPrice() = this.amount * this.product.price

    private fun Order.validateState(vararg allowedStates: OrderState) {
        val allowedSet = EnumSet.copyOf(allowedStates.asList())
        if (!allowedSet.contains(this.state)) {
            throw IllegalOrderStateException(checkNotNull(this.id), this.state, allowedSet)
        }
    }
}