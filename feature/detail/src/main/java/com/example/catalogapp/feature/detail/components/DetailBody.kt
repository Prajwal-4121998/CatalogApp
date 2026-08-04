package com.example.catalogapp.feature.detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.domain.product.Product
import com.example.catalogapp.feature.detail.DetailIntent
import com.example.catalogapp.feature.detail.DetailUiState

@Composable
internal fun DetailBody(
    product: Product,
    uiState: DetailUiState,
    onIntent: (DetailIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = CatalogTheme.spacing
    androidx.compose.foundation.layout.Column(modifier = modifier) {
        // Hero image — full bleed, 4:5 aspect ratio
        DetailHeroImage(
            imageUrl = product.imageUrl,
            title = product.title
        )
        // Content card overlapping hero by -mt-12 (48dp)
        Box(
            modifier = Modifier.padding(horizontal = spacing.md)
        ) {
            DetailContentCard(
                product = product,
                isDescriptionExpanded = uiState.isDescriptionExpanded,
                onToggleDescription = { onIntent(DetailIntent.ToggleDescription) }
            )
        }
        // Reviews section — OUTSIDE the white card, in surface background
        Box(
            modifier = Modifier.padding(
                top = spacing.md,
                start = spacing.md,
                end = spacing.md,
                bottom = spacing.xl
            )
        ) {
            DetailReviewsSection()
        }
    }
}
