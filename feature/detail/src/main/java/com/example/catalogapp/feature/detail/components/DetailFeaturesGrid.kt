package com.example.catalogapp.feature.detail.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.domain.product.Product
import com.example.catalogapp.feature.detail.ProductFeature
import com.example.catalogapp.feature.detail.ProductFeatureProvider
import com.example.catalogapp.feature.detail.previewProduct

private const val FEATURE_TILE_CORNER = 16

@Composable
internal fun DetailFeaturesGrid(
    product: Product,
    modifier: Modifier = Modifier
) {
    val spacing = CatalogTheme.spacing
    val features = ProductFeatureProvider.getFeaturesForProduct(
        category = product.category,
        rating = product.rating
    )

    // Fixed 2x2 grid using two Rows — no LazyVerticalGrid needed for 4 static items
    // LazyVerticalGrid inside verticalScroll causes unbounded height conflict
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            FeatureTile(
                feature = features[0],
                modifier = Modifier.weight(1f)
            )
            FeatureTile(
                feature = features[1],
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            FeatureTile(
                feature = features[2],
                modifier = Modifier.weight(1f)
            )
            FeatureTile(
                feature = features[3],
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FeatureTile(
    feature: ProductFeature,
    modifier: Modifier = Modifier
) {
    val spacing = CatalogTheme.spacing
    // Stitch: bg-surface-container-low p-md rounded-2xl border border-outline-variant/30
    Surface(
        modifier = modifier.border(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(FEATURE_TILE_CORNER.dp)
        ),
        shape = RoundedCornerShape(FEATURE_TILE_CORNER.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.xs)
        ) {
            Text(
                text = feature.icon,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = feature.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = feature.value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun DetailFeaturesGridPreview() {
    CatalogTheme { DetailFeaturesGrid(product = previewProduct) }
}
