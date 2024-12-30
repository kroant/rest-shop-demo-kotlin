package cz.kromer.restshopdemo.controller

import cz.kromer.restshopdemo.dto.CreateOrderDto
import cz.kromer.restshopdemo.dto.OrderResponseDto
import cz.kromer.restshopdemo.dto.error.ErrorResponseDto
import cz.kromer.restshopdemo.service.OrderService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService
) {

    @GetMapping
    fun findAll() = orderService.findAll()

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = OrderResponseDto::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResponseDto::class))])
    fun getById(@PathVariable id: UUID) = orderService.getById(id)

    @PostMapping
    @ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = OrderResponseDto::class))])
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResponseDto::class))])
    fun create(@Valid @RequestBody order: CreateOrderDto) = orderService.getById(orderService.create(order))

    @PutMapping("/{id}/cancel")
    @ResponseStatus(NO_CONTENT)
    @ApiResponse(responseCode = "204", content = [Content()])
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResponseDto::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResponseDto::class))])
    fun cancel(@PathVariable id: UUID) = orderService.cancel(id)

    @PutMapping("/{id}/pay")
    @ResponseStatus(NO_CONTENT)
    @ApiResponse(responseCode = "204", content = [Content()])
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResponseDto::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResponseDto::class))])
    fun pay(@PathVariable id: UUID) = orderService.pay(id)
}