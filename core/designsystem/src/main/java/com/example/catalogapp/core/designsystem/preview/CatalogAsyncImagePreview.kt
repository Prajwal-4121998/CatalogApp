package com.example.catalogapp.core.designsystem.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.theme.CatalogTheme

// This mirrors CatalogAsyncImage's PLACEHOLDER state visually —
// can't preview the real Coil pipeline since no network call runs in @Preview.
@Composable
private fun PlaceholderStatePreview() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    )
}

// Mirrors CatalogAsyncImage's ERROR state visually — tonal box + muted icon overlay
@Composable
private fun ErrorStatePreview() {
    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        )
        Icon(
            imageVector = Icons.Default.BrokenImage,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = 0x20)
@Composable
internal fun CatalogAsyncImageStatesPreview() {
    CatalogTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            Column {
                PlaceholderStatePreview()
            }
            Column {
                ErrorStatePreview()
            }
        }
    }
}

