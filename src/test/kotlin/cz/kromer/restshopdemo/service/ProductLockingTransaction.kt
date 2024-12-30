package cz.kromer.restshopdemo.service

import cz.kromer.restshopdemo.repository.ProductRepository
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.lang.Thread.sleep
import java.util.UUID
import java.util.concurrent.CountDownLatch

@Component
@Scope(SCOPE_PROTOTYPE)
class ProductLockingTransaction(
    private val productRepository: ProductRepository,
) {

    private val log = getLogger(javaClass)

    private val latch = CountDownLatch(1)

    @Transactional
    fun lockAndSleep(productId: UUID, sleepMillis: Long) {
        log.info("Locking Product id: {}", productId)
        productRepository.findAndLockById(productId)
        log.info("Product locked. Product id: {}", productId)

        latch.countDown()

        sleep(sleepMillis)
        log.info("Releasing lock. Product id: {}", productId)
    }

    fun waitUntilProductLocked() {
        latch.await()
    }
}