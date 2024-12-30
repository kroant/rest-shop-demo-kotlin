package cz.kromer.restshopdemo.dto.error

data class ErrorResponseDto(
    val errorCode: ErrorResponseCode,
    val errorDetails: List<ErrorDetailDto>
)