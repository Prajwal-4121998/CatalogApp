package com.example.catalogapp.feature.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.domain.product.Product
import com.example.catalogapp.feature.detail.previewProduct

private const val CARD_CORNER = 24
private const val CARD_OVERLAP_DP = 48

@Composable
internal fun DetailContentCard(
    product: Product,
    isDescriptionExpanded: Boolean,
    onToggleDescription: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = CatalogTheme.spacing
    // Stitch: -mt-12 (48dp negative offset), rounded-3xl, surface-container-lowest, p-lg
    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = (-CARD_OVERLAP_DP).dp),
        shape = RoundedCornerShape(CARD_CORNER.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Product header: brand, title, rating, availability
            DetailProductHeader(product = product)
            Spacer(modifier = Modifier.height(spacing.lg))
            // Price row
            DetailPriceSection(price = product.price)
            Spacer(modifier = Modifier.height(spacing.lg))
            // Features bento 2-column grid
            DetailFeaturesGrid(product = product)
            Spacer(modifier = Modifier.height(spacing.xl))
            // Collapsible description
            DetailDescriptionSection(
                description = product.description,
                isExpanded = isDescriptionExpanded,
                onToggle = onToggleDescription
            )
            Spacer(modifier = Modifier.height(spacing.md))
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun DetailContentCardPreview() {
    CatalogTheme {
        DetailContentCard(
            product = previewProduct,
            isDescriptionExpanded = false,
            onToggleDescription = {}
        )
    }
}
