package cz.kromer.restshopdemo.dto.validation

import cz.kromer.restshopdemo.dto.ProductDto
import cz.kromer.restshopdemo.utils.isScaleValid
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ProductStockMaxScaleValidator : ConstraintValidator<ProductStockMaxScale, ProductDto> {

    override fun isValid(value: ProductDto?, context: ConstraintValidatorContext?) =
        value?.unit == null || value.stock == null || value.unit.isScaleValid(value.stock)
}