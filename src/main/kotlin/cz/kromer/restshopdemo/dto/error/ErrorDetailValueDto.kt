package cz.kromer.restshopdemo.dto.error

data class ErrorDetailValueDto(
    val type: ErrorDetailValueType,
    val value: String
)