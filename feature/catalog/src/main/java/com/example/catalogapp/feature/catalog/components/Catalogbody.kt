package com.example.catalogapp.feature.catalog.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.catalogapp.core.designsystem.components.CatalogEmptyState
import com.example.catalogapp.core.designsystem.components.CatalogErrorState
import com.example.catalogapp.core.designsystem.components.CatalogLoadingState
import com.example.catalogapp.feature.catalog.CatalogIntent
import com.example.catalogapp.feature.catalog.CatalogUiState

// ─── Body — pull-to-refresh + category chips + content states ────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CatalogBody(
    uiState: CatalogUiState,
    onIntent: (CatalogIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { onIntent(CatalogIntent.RefreshProducts) },
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (uiState.categories.size > 1) {
                CategoryChips(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = { category ->
                        onIntent(CatalogIntent.SelectCategory(category))
                    }
                )
            }
            when {
                uiState.showLoading -> CatalogLoadingState()
                uiState.showError -> CatalogErrorState(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { onIntent(CatalogIntent.RetryLoad) }
                )
                uiState.showEmpty -> CatalogEmptyState(
                    message = "No products available right now."
                )
                uiState.showProducts -> ProductGrid(
                    products = uiState.filteredProducts,
                    onProductClick = { id -> onIntent(CatalogIntent.ProductClicked(id)) }
                )
            }
        }
    }
}
