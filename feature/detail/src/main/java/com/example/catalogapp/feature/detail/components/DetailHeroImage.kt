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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.catalogapp.core.designsystem.theme.CatalogTheme

private const val HERO_ASPECT_WIDTH = 4f
private const val HERO_ASPECT_HEIGHT = 5f
private const val GRADIENT_START_FRACTION = 0.5f

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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
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
