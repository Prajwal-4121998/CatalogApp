package com.example.catalogapp.data.product.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val imageUrl: String,
    val rating: Float,
    val ratingCount: Int,
    val cachedAt: Long = System.currentTimeMillis()
)
