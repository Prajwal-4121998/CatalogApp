package com.example.catalogapp.feature.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.domain.product.Product
import com.example.catalogapp.feature.detail.previewProduct
import java.util.Locale

private const val STAR_SIZE = 18
private const val BADGE_DOT_SIZE = 8
private const val EMERALD_LIGHT = 0xFFD1FAE5
private const val EMERALD_DARK = 0xFF065F46
private const val EMERALD_DOT = 0xFF059669

@Composable
internal fun DetailProductHeader(
    product: Product,
    modifier: Modifier = Modifier
) {
    val spacing = CatalogTheme.spacing
    Column(
        modifier = modifier.padding(spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        // Stitch: text-primary font-label-lg tracking-widest uppercase
        Text(
            text = product.category.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.Medium
        )
        // Stitch: font-headline-lg-mobile = Hanken Grotesk 28sp bold
        Text(
            text = product.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RatingPill(rating = product.rating)
            InStockBadge()
        }
    }
}

@Composable
private fun RatingPill(rating: Float) {
    val spacing = CatalogTheme.spacing
    // Stitch: bg-surface-container-high px-3 py-1 rounded-full
    Row(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
    ) {
        // Stitch: filled amber star 18sp
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(STAR_SIZE.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = String.format(Locale.getDefault(), "%.1f", rating),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "(${(rating * 200).toInt()} reviews)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InStockBadge() {
    val spacing = CatalogTheme.spacing
    // Stitch: bg-emerald-100 text-emerald-800 px-3 py-1 rounded-full with pulse dot
    Row(
        modifier = Modifier
            .background(
                color = Color(EMERALD_LIGHT),
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
    ) {
        Box(
            modifier = Modifier
                .size(BADGE_DOT_SIZE.dp)
                .background(color = Color(EMERALD_DOT), shape = CircleShape)
        )
        Text(
            text = "In Stock",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color(EMERALD_DARK)
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun DetailProductHeaderPreview() {
    CatalogTheme { DetailProductHeader(product = previewProduct) }
}
