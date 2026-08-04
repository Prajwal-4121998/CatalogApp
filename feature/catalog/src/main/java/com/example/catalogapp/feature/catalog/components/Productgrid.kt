package com.example.catalogapp.feature.catalog.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.catalogapp.core.designsystem.components.rememberDebouncedOnClick
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.domain.product.Product

// ─── Product grid ─────────────────────────────────────────────────────────────
@Composable
internal fun ProductGrid(
    products: List<Product>,
    onProductClick: (Int) -> Unit
) {
    val spacing = CatalogTheme.spacing
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(spacing.md),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = products,
            key = { product -> product.id }
        ) { product ->
            ProductCard(
                product = product,
                onClick = rememberDebouncedOnClick { onProductClick(product.id) }
            )
        }
    }
}
