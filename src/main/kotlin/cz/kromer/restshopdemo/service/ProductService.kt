package cz.kromer.restshopdemo.service

import cz.kromer.restshopdemo.dto.ProductDto
import cz.kromer.restshopdemo.exception.RootEntityNotFoundException
import cz.kromer.restshopdemo.mapper.ProductDtoMapper
import cz.kromer.restshopdemo.mapper.ProductMapper
import cz.kromer.restshopdemo.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val productMapper: ProductMapper,
    private val productDtoMapper: ProductDtoMapper
) {

    @Transactional(readOnly = true)
    fun findAll() = productRepository.findAll().map { productDtoMapper.mapFrom(it) }

    @Transactional(readOnly = true)
    fun getById(id: UUID) = productRepository.findByIdOrNull(id)
            ?.let { productDtoMapper.mapFrom(it) }
            ?: throw RootEntityNotFoundException(id)

    @Transactional
    fun save(product: ProductDto): UUID {
        var entity = productMapper.mapFrom(product)
        entity = productRepository.save(entity)
        return checkNotNull(entity.id)
    }

    @Transactional
    fun update(id: UUID, product: ProductDto) {
        val entity = productRepository.findByIdOrNull(id) ?: throw RootEntityNotFoundException(id)
        productMapper.mapToProduct(product, entity)
    }

    @Transactional
    fun delete(id: UUID) {
        val entity = productRepository.findByIdOrNull(id) ?: throw RootEntityNotFoundException(id)
        entity.deleted = true
    }
}