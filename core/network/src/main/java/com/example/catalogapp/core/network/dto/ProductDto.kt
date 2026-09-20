package com.example.catalogapp.core.network.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("description")
    val description: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("image")
    val imageUrl: String,
    @SerializedName("rating")
    val rating: RatingDto?
)

data class RatingDto(
    @SerializedName("rate")
    val rate: Float,
    @SerializedName("count")
    val count: Int
)
