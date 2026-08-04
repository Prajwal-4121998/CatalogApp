package com.example.catalogapp.feature.catalog.components

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.catalogapp.core.designsystem.components.ProductCardSkeleton
import com.example.catalogapp.core.designsystem.theme.LocalSpacing

private const val SKELETON_ITEM_COUNT = 4
private const val GRID_COLUMN_COUNT = 2

@Composable
internal fun ProductGridSkeleton(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMN_COUNT),
        modifier = modifier,
        contentPadding = PaddingValues(spacing.md),
        horizontalArrangement = spacedBy(spacing.sm),
        verticalArrangement = spacedBy(spacing.sm)
    ) {
        items(SKELETON_ITEM_COUNT) {
            ProductCardSkeleton()
        }
    }
}

