package cz.kromer.restshopdemo.dto.validation

import cz.kromer.restshopdemo.dto.OrderItemDto
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.util.HashSet.newHashSet
import java.util.UUID

class UniqueOrderProductValidator : ConstraintValidator<UniqueOrderProduct, List<OrderItemDto?>?> {

    override fun isValid(items: List<OrderItemDto?>?, context: ConstraintValidatorContext?): Boolean {
        if (!items.isNullOrEmpty()) {
            val uuidSet = newHashSet<UUID>(items.size)
            for (item in items) {
                if (item?.product?.id != null) {
                    if (!uuidSet.add(item.product.id)) {
                        return false
                    }
                }
            }
        }
        return true
    }
}