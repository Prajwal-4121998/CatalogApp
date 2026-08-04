package com.example.catalogapp.feature.catalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.components.CatalogAsyncImage
import com.example.catalogapp.core.designsystem.components.ProductCardDefaults
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.core.designsystem.theme.Spacing
import com.example.catalogapp.domain.product.Product
import java.util.Locale

private const val REVIEW_COUNT_MULTIPLIER = 100

// ─── Product card — matches Stitch exactly ────────────────────────────────────
@Composable
internal fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    val spacing = CatalogTheme.spacing
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            // Stitch: surface-container-lowest for card background
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            ProductImageSection(product = product, spacing = spacing)
            ProductInfoSection(product = product, spacing = spacing)
        }
    }
}

// ─── Image with badge and wishlist overlay ────────────────────────────────────
@Composable
private fun ProductImageSection(
    product: Product,
    spacing: Spacing
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(
                ProductCardDefaults.IMAGE_ASPECT_WIDTH /
                        ProductCardDefaults.IMAGE_ASPECT_HEIGHT
            )
    ) {
        CatalogAsyncImage(
            imageUrl = product.imageUrl,
            contentDescription = product.title,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        // Stitch: discount badge top-left, secondary-container color
        Box(
            modifier = Modifier
                .padding(spacing.sm)
                .align(Alignment.TopStart)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = spacing.sm, vertical = spacing.xs)
        ) {
            Text(
                text = "15% OFF",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        // Stitch: wishlist button top-right, semi-transparent white
        Box(
            modifier = Modifier
                .padding(spacing.sm)
                .align(Alignment.TopEnd)
                .size(32.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "Add to wishlist",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── Title, rating, price ──────────────────────────────────────────────────────
@Composable
private fun ProductInfoSection(
    product: Product,
    spacing: Spacing
) {
    Column(
        modifier = Modifier.padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.xs)
    ) {
        // Title — Stitch: body-md, 2 lines max
        Text(
            text = product.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
        RatingRow(rating = product.rating)
        // Price — Stitch: title-lg style, primary color
        Text(
            // Explicit Locale.US: avoids ImplicitDefaultLocale and keeps digits
            // consistent regardless of device locale (comma vs. dot separators).
            text = "₹${String.format(Locale.US, "%.0f", product.price)}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ─── Rating row — matches Stitch: number first, filled star, count ────────────
@Composable
private fun RatingRow(rating: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Stitch: rating number in secondary color
        Text(
            // Explicit Locale.US: avoids ImplicitDefaultLocale.
            text = String.format(Locale.US, "%.1f", rating),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        // Stitch: filled star icon in secondary color
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        // Stitch: review count in outline-variant color
        Text(
            text = "(${(rating * REVIEW_COUNT_MULTIPLIER).toInt()})",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}
