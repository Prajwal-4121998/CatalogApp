package com.example.catalogapp.data.product.mapper

import com.example.catalogapp.core.network.dto.ProductDto
import com.example.catalogapp.data.product.local.entity.ProductEntity
import com.example.catalogapp.domain.product.Product

// ─── Network DTO → Domain Model ───────────────────────────────────────────────
fun ProductDto.toDomain(): Product = Product(
    id = id,
    title = title,
    price = price,
    description = description,
    category = category,
    imageUrl = imageUrl,
    rating = rating?.rate ?: 0f
)

// ─── Network DTO → Room Entity ────────────────────────────────────────────────
fun ProductDto.toEntity(): ProductEntity = ProductEntity(
    id = id,
    title = title,
    price = price,
    description = description,
    category = category,
    imageUrl = imageUrl,
    rating = rating?.rate ?: 0f,
    ratingCount = rating?.count ?: 0
)

// ─── Room Entity → Domain Model ───────────────────────────────────────────────
fun ProductEntity.toDomain(): Product = Product(
    id = id,
    title = title,
    price = price,
    description = description,
    category = category,
    imageUrl = imageUrl,
    rating = rating
)

// ─── List extensions ──────────────────────────────────────────────────────────
fun List<ProductDto>.toEntityList(): List<ProductEntity> = map { it.toEntity() }
fun List<ProductEntity>.toDomainList(): List<Product> = map { it.toDomain() }
