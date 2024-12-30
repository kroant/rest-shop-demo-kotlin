package cz.kromer.restshopdemo.repository

import cz.kromer.restshopdemo.entity.Order
import jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
import jakarta.persistence.QueryHint
import org.hibernate.cfg.AvailableSettings.JAKARTA_LOCK_TIMEOUT
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import java.time.Instant
import java.util.UUID

interface OrderRepository : JpaRepository<Order, UUID> {

    @Lock(PESSIMISTIC_WRITE)
    @QueryHints(value = [QueryHint(name = JAKARTA_LOCK_TIMEOUT, value = "2000")])
    fun findAndLockById(id: UUID): Order?

    @Query("select o.id from Order o where o.createdOn < :before and o.state = 'NEW'")
    fun findNewOrdersBefore(before: Instant): List<UUID>
}