package com.example.catalogapp.feature.search

import com.example.catalogapp.domain.product.Product

data class SearchUiState(
    val query: String = "",
    val allProducts: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val availableCategories: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false,
    val error: String? = null
)

sealed class SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent()
    data object ClearQuery : SearchIntent()
    data class ProductClicked(val productId: Int) : SearchIntent()
    data class RecentSearchClicked(val query: String) : SearchIntent()
    data object ClearRecentSearches : SearchIntent()
    data object RetryLoad : SearchIntent()
}

sealed class SearchEffect {
    data class NavigateToDetail(val productId: Int) : SearchEffect()
    data object DismissKeyboard : SearchEffect()
}
