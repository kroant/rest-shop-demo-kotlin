package cz.kromer.restshopdemo.controller

import cz.kromer.restshopdemo.dto.ProductDto
import cz.kromer.restshopdemo.dto.error.ErrorResponseDto
import cz.kromer.restshopdemo.service.ProductService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.DeleteMapping
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
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun findAll() = productService.findAll()

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = ProductDto::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResponseDto::class))])
    fun getById(@PathVariable id: UUID) = productService.getById(id)

    @PostMapping
    @ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = ProductDto::class))])
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResponseDto::class))])
    fun save(@Valid @RequestBody product: ProductDto) = productService.getById(productService.save(product))

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = ProductDto::class))])
    @ApiResponse(responseCode = "400", content = [Content(schema = Schema(implementation = ErrorResponseDto::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResponseDto::class))])
    fun update(@PathVariable id: UUID, @Valid @RequestBody product: ProductDto): ProductDto {
        productService.update(id, product)
        return productService.getById(id)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiResponse(responseCode = "204", content = [Content()])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResponseDto::class))])
    fun delete(@PathVariable id: UUID) = productService.delete(id)
}