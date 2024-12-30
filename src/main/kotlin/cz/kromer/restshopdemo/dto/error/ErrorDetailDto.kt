package cz.kromer.restshopdemo.dto.error

import java.util.UUID

data class ErrorDetailDto(
    val entityId: UUID? = null,
    val field: String? = null,
    val message: String? = null,
    val values: List<ErrorDetailValueDto> = emptyList()
)