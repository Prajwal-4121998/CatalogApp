package com.example.catalogapp.core.network.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    @SerializedName("image")
    val imageUrl: String,
    val rating: RatingDto?
)

data class RatingDto(
    val rate: Float,
    val count: Int
)
