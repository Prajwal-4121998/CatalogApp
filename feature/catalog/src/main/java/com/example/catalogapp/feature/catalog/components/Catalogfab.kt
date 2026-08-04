package com.example.catalogapp.feature.catalog.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.components.rememberDebouncedOnClick

// ─── FAB — matches Stitch: filter tune icon, secondary-container ─────────────
@Composable
internal fun CatalogFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = rememberDebouncedOnClick(onClick),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Filter"
        )
    }
}
