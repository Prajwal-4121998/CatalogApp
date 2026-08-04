package com.example.catalogapp.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

private const val IMAGE_CROSSFADE_DURATION_MS = 300

data class CatalogImagePlaceholders(
    val placeholder: Painter,
    val errorBackground: Painter
) {
    companion object {
        @Composable
        fun default(): CatalogImagePlaceholders {
            val tonal = ColorPainter(MaterialTheme.colorScheme.surfaceContainerHigh)
            return CatalogImagePlaceholders(placeholder = tonal, errorBackground = tonal)
        }
    }
}

@Composable
fun CatalogAsyncImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholders: CatalogImagePlaceholders = CatalogImagePlaceholders.default()
) {
    var isError by remember(imageUrl) { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(IMAGE_CROSSFADE_DURATION_MS)
                .build(),
            contentDescription = contentDescription,
            placeholder = placeholders.placeholder,
            error = placeholders.errorBackground,
            onError = { isError = true },
            onSuccess = { isError = false },
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize(),
        )

        if (isError) {
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .then(Modifier),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

