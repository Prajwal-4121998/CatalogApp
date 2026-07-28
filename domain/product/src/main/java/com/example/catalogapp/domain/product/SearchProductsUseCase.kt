package com.example.catalogapp.domain.product

import javax.inject.Inject

class SearchProductsUseCase @Inject constructor() {
    operator fun invoke(products: List<Product>, query: String): List<Product> {
        if (query.isBlank()) return emptyList()
        return products.filter { product ->
            product.title.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true)
        }
    }
}

