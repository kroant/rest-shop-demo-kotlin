package cz.kromer.restshopdemo.utils

import cz.kromer.restshopdemo.dto.QuantityUnit
import java.math.BigDecimal

fun QuantityUnit.isScaleValid(value: BigDecimal) = value.stripTrailingZeros().scale() <= this.maxScale