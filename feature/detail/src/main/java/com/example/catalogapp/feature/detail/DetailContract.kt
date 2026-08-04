package com.example.catalogapp.feature.detail

import com.example.catalogapp.domain.product.Product

// ─── UI State ─────────────────────────────────────────────────────────────────
data class DetailUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isWishlisted: Boolean = false,
    val isDescriptionExpanded: Boolean = false
) {
    val showLoading: Boolean get() = isLoading && product == null
    val showError: Boolean get() = error != null && !isLoading && product == null
    val showContent: Boolean get() = product != null
}

// ─── Intent ───────────────────────────────────────────────────────────────────
sealed class DetailIntent {
    data class LoadProduct(val productId: Int) : DetailIntent()
    object RetryLoad : DetailIntent()
    object NavigateBack : DetailIntent()
    object ToggleWishlist : DetailIntent()
    object AddToCart : DetailIntent()
    object ToggleDescription : DetailIntent()
    object ShareProduct : DetailIntent()
}

// ─── Effect ───────────────────────────────────────────────────────────────────
sealed class DetailEffect {
    object NavigateBack : DetailEffect()
    data class ShowSnackbar(val message: String) : DetailEffect()
    data class ShareProduct(val title: String, val price: Double) : DetailEffect()
}
