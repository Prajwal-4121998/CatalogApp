package com.example.catalogapp.core.designsystem.components

import androidx.compose.ui.unit.dp

// Promoted from feature:catalog into core:designsystem so BOTH
// ProductCard (real) and ProductCardSkeleton (loading) reference the same constants.
// This is the single source of truth for product card dimensions.
object ProductCardDefaults {
    const val IMAGE_ASPECT_WIDTH = 4f
    const val IMAGE_ASPECT_HEIGHT = 5f
    val CORNER_RADIUS = 12.dp
}
