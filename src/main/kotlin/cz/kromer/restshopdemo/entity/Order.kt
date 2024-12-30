package cz.kromer.restshopdemo.entity

import cz.kromer.restshopdemo.dto.OrderState
import jakarta.persistence.CascadeType.PERSIST
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "order_")
class Order(

    @Id
    @GeneratedValue
    val id: UUID?,

    @Enumerated(STRING)
    var state: OrderState,

    var price: BigDecimal,

    @OneToMany(mappedBy = "order", cascade = [PERSIST])
    val items: List<OrderItem>,

    @CreationTimestamp
    @Column(updatable = false)
    val createdOn: Instant?
)