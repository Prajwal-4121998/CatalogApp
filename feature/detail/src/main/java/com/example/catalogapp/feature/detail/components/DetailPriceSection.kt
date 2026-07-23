package com.example.catalogapp.feature.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import java.util.Locale

private const val DISCOUNT_PERCENT = 18
private const val PERCENT_DIVISOR = 100.0

@Composable
internal fun DetailPriceSection(
    price: Double,
    modifier: Modifier = Modifier
) {
    val spacing = CatalogTheme.spacing
    val originalPrice = price * (1 + DISCOUNT_PERCENT / PERCENT_DIVISOR)

    // Stitch: flex items-baseline gap-md mt-lg
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.lg),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        // Stitch: text-headline-lg-mobile font-bold text-on-surface
        Text(
            text = "₹${String.format(Locale.getDefault(), "%,.0f", price)}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        // Stitch: text-on-surface-variant line-through text-title-lg
        Text(
            text = "₹${String.format(Locale.getDefault(), "%,.0f", originalPrice)}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textDecoration = TextDecoration.LineThrough
        )
        // Stitch: text-secondary font-bold text-title-lg
        Text(
            text = "$DISCOUNT_PERCENT% OFF",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun DetailPriceSectionPreview() {
    CatalogTheme { DetailPriceSection(price = 12999.0) }
}
