package com.example.catalogapp.feature.catalog

import com.example.catalogapp.domain.product.Product

// ─── UI State — single source of truth for the catalog screen ─────────────────
// Data class (not sealed) because multiple states can be true simultaneously:
// isRefreshing=true while products are still visible during pull-to-refresh
data class CatalogUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "All"
) {
    // Derived state — computed from existing state, never stored separately
    // This prevents impossible state combinations like isLoading=true + showProducts=true
    val showLoading: Boolean get() = isLoading && products.isEmpty()
    val showError: Boolean get() = error != null && !isLoading && products.isEmpty()
    val showEmpty: Boolean get() = !isLoading && error == null && products.isEmpty()
    val showProducts: Boolean get() = products.isNotEmpty()

    // Categories derived from product list — no separate state needed
    val categories: List<String> get() = listOf("All") +
            products.map { it.category }
                .distinct()
                .sorted()

    // Filtered products based on selected category
    val filteredProducts: List<Product> get() = if (selectedCategory == "All") {
        products
    } else {
        products.filter { it.category == selectedCategory }
    }
}

// ─── Intent — every possible user action, explicitly named ────────────────────
sealed class CatalogIntent {
    object LoadProducts : CatalogIntent()
    object RefreshProducts : CatalogIntent()
    object RetryLoad : CatalogIntent()
    object SearchClicked : CatalogIntent()
    data class SelectCategory(val category: String) : CatalogIntent()
    data class ProductClicked(val productId: Int) : CatalogIntent()
}

// ─── Effect — one-time events, consumed once, never replayed ──────────────────
sealed class CatalogEffect {
    data class NavigateToDetail(val productId: Int) : CatalogEffect()
    object NavigateToSearch : CatalogEffect()
    data class ShowSnackbar(val message: String) : CatalogEffect()
}
