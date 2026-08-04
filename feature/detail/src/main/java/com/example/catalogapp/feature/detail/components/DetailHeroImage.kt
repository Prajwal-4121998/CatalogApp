package com.example.catalogapp.feature.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.catalogapp.core.designsystem.components.CatalogAsyncImage
import com.example.catalogapp.core.designsystem.components.DetailHeroImageDefaults.GRADIENT_START_FRACTION
import com.example.catalogapp.core.designsystem.components.DetailHeroImageDefaults.HERO_ASPECT_HEIGHT
import com.example.catalogapp.core.designsystem.components.DetailHeroImageDefaults.HERO_ASPECT_WIDTH
import com.example.catalogapp.core.designsystem.theme.CatalogTheme

@Composable
internal fun DetailHeroImage(
    imageUrl: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            // Stitch: aspect-[4/5] same as product card
            .aspectRatio(HERO_ASPECT_WIDTH / HERO_ASPECT_HEIGHT)
    ) {
        CatalogAsyncImage(
            imageUrl = imageUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxSize()
        )
        // Stitch: bg-gradient-to-t from-surface via-transparent to-transparent opacity-60
        // Gradient fades from surface color at bottom to transparent at top
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.Transparent,
                            GRADIENT_START_FRACTION to Color.Transparent,
                            1f to MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        )
                    )
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun DetailHeroImagePreview() {
    CatalogTheme {
        DetailHeroImage(imageUrl = "", title = "Preview Product")
    }
}
