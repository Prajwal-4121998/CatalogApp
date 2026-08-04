package com.example.catalogapp.feature.catalog.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.components.rememberDebouncedOnClick
import com.example.catalogapp.core.designsystem.theme.CatalogTheme

// ─── Category chips — matches Stitch exactly ─────────────────────────────────
@Composable
internal fun CategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val spacing = CatalogTheme.spacing
    LazyRow(
        contentPadding = PaddingValues(horizontal = spacing.md),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = Modifier.padding(vertical = spacing.sm)
    ) {
        items(categories) { category ->
            CategoryChip(
                label = category.replaceFirstChar { it.uppercase() },
                isSelected = category == selectedCategory,
                onClick = rememberDebouncedOnClick { onCategorySelected(category) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val spacing = CatalogTheme.spacing
    // Stitch: tertiary-container for active, surface-container-high for inactive
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.sm)
        )
    }
}
