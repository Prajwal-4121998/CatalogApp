package com.example.catalogapp.feature.catalog.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.catalogapp.core.designsystem.components.CatalogEmptyState
import com.example.catalogapp.core.designsystem.components.CatalogErrorState
import com.example.catalogapp.core.designsystem.components.CategoryChipRowSkeleton
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.feature.catalog.CatalogIntent
import com.example.catalogapp.feature.catalog.CatalogUiState

private const val CROSSFADE_DURATION_MS = 400

// Represents WHICH content state we're in — used as the AnimatedContent key.
// Without this, AnimatedContent can't distinguish "loading -> success" from
// "loading -> error", since both are just showLoading going true -> false.
private enum class CatalogContentState {
    Loading, Error, Empty, Products
}

private fun CatalogUiState.contentState(): CatalogContentState = when {
    showLoading -> CatalogContentState.Loading
    showError -> CatalogContentState.Error
    showEmpty -> CatalogContentState.Empty
    else -> CatalogContentState.Products
}

// ─── Body — pull-to-refresh + category chips + content states ────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CatalogBody(
    uiState: CatalogUiState,
    onIntent: (CatalogIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = CatalogTheme.spacing
    val state = uiState.contentState()

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = {
            onIntent(CatalogIntent.RefreshProducts)
        },
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (state) {
                CatalogContentState.Loading -> CategoryChipRowSkeleton(
                    modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.md)
                )

                else -> if (uiState.categories.size > 1) {
                    CategoryChips(
                        categories = uiState.categories,
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { category ->
                            onIntent(
                                CatalogIntent.SelectCategory(
                                    category
                                )
                            )
                        }
                    )
                }
            }

            AnimatedContent(
                targetState = uiState.contentState(),
                transitionSpec = {
                    fadeIn(animationSpec = tween(CROSSFADE_DURATION_MS)) togetherWith
                            fadeOut(animationSpec = tween(CROSSFADE_DURATION_MS))
                },
                label = "catalog_content_crossfade"
            ) { state ->
                when (state) {
                    CatalogContentState.Loading -> ProductGridSkeleton()
                    CatalogContentState.Error -> CatalogErrorState(
                        message = uiState.error ?: "Unknown error",
                        onRetry = { onIntent(CatalogIntent.RetryLoad) }
                    )

                    CatalogContentState.Empty -> CatalogEmptyState(
                        message = "No products available right now."
                    )

                    CatalogContentState.Products -> ProductGrid(
                        products = uiState.filteredProducts,
                        onProductClick = { id -> onIntent(CatalogIntent.ProductClicked(id)) }
                    )
                }
            }
        }
    }
}
