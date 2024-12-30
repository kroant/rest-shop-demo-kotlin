package cz.kromer.restshopdemo.controller

import cz.kromer.restshopdemo.dto.error.ErrorDetailDto
import cz.kromer.restshopdemo.dto.error.ErrorDetailValueDto
import cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.ALLOWED_STATE
import cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.CURRENT_STATE
import cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.MISSING_AMOUNT
import cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.VALIDATION_CODE
import cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ENTITY_NOT_FOUND
import cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ILLEGAL_AMOUNT_SCALE
import cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ILLEGAL_ORDER_STATE
import cz.kromer.restshopdemo.dto.error.ErrorResponseCode.PRODUCT_STOCK_SHORTAGE
import cz.kromer.restshopdemo.dto.error.ErrorResponseCode.REQUEST_VALIDATION_ERROR
import cz.kromer.restshopdemo.dto.error.ErrorResponseDto
import cz.kromer.restshopdemo.exception.AssociatedEntityNotFoundException
import cz.kromer.restshopdemo.exception.IllegalAmountScaleException
import cz.kromer.restshopdemo.exception.IllegalOrderStateException
import cz.kromer.restshopdemo.exception.ProductShortageException
import cz.kromer.restshopdemo.exception.ProductStockShortageDto
import cz.kromer.restshopdemo.exception.RootEntityNotFoundException
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.http.ResponseEntity.status
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.UUID

@RestControllerAdvice
class RestResponseEntityExceptionHandler {

    @ExceptionHandler(RootEntityNotFoundException::class)
    fun handleRootEntityNotFound(exception: RootEntityNotFoundException): ResponseEntity<ErrorResponseDto> =
        status(NOT_FOUND).body(mapToEntityNotFoundResponse(exception.id))

    @ExceptionHandler(AssociatedEntityNotFoundException::class)
    fun handleAssociatedEntityNotFound(exception: AssociatedEntityNotFoundException): ResponseEntity<ErrorResponseDto> =
        badRequest().body(mapToEntityNotFoundResponse(exception.id))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponseDto> =
        badRequest().body(
            ErrorResponseDto(
                errorCode = REQUEST_VALIDATION_ERROR,
                errorDetails = exception.allErrors.map(::mapToErrorDetail)
            ))

    @ExceptionHandler(IllegalOrderStateException::class)
    fun handleIllegalOrderState(exception: IllegalOrderStateException): ResponseEntity<ErrorResponseDto> =
        badRequest().body(
            ErrorResponseDto(
                errorCode = ILLEGAL_ORDER_STATE,
                errorDetails = listOf(ErrorDetailDto(
                    entityId = exception.id,
                    values = mapToDetailValues(exception)
                ))
            ))

    @ExceptionHandler(IllegalAmountScaleException::class)
    fun handleIllegalAmountScale(exception: IllegalAmountScaleException): ResponseEntity<ErrorResponseDto> =
        badRequest().body(
            ErrorResponseDto(
                errorCode = ILLEGAL_AMOUNT_SCALE,
                errorDetails = listOf(ErrorDetailDto(entityId = exception.productId))
            ))

    @ExceptionHandler(ProductShortageException::class)
    fun handleProductShortage(exception: ProductShortageException): ResponseEntity<ErrorResponseDto> =
        badRequest().body(
            ErrorResponseDto(
                errorCode = PRODUCT_STOCK_SHORTAGE,
                errorDetails = exception.productShortages.map(::mapToErrorDetail)
            ))

    private fun mapToEntityNotFoundResponse(id: UUID) =
        ErrorResponseDto(
            errorCode = ENTITY_NOT_FOUND,
            errorDetails = listOf(
                ErrorDetailDto(
                    entityId = id
                )
            )
        )

    private fun mapToErrorDetail(objectError: ObjectError) =
        ErrorDetailDto(
            field = if (objectError is FieldError) objectError.field else null,
            message = objectError.defaultMessage,
            values = listOf(
                ErrorDetailValueDto(
                    type = VALIDATION_CODE,
                    value = objectError.code
                )
            )
        )

    private fun mapToDetailValues(exception: IllegalOrderStateException) =
        listOf(
            ErrorDetailValueDto(
                type = CURRENT_STATE,
                value = exception.currentState.name
            )
        ) + exception.allowedStates.map {
            ErrorDetailValueDto(
                type = ALLOWED_STATE,
                value = it.name
            )
        }

    private fun mapToErrorDetail(shortage: ProductStockShortageDto) =
        ErrorDetailDto(
            entityId = shortage.product.id,
            values = listOf(
                ErrorDetailValueDto(
                    type = MISSING_AMOUNT,
                    value = shortage.missingAmount.toPlainString()
                )
            )
        )
}