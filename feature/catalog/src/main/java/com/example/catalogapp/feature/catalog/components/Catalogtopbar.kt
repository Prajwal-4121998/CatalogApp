package com.example.catalogapp.feature.catalog.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.example.catalogapp.core.designsystem.components.rememberDebouncedOnClick

// ─── Top App Bar — matches Stitch: menu icon + title + search ─────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CatalogTopBar(onSearchClick: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(rememberDebouncedOnClick { }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        title = {
            Text(
                text = "CatalogApp",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        },
        actions = {
            IconButton(onClick = rememberDebouncedOnClick(onSearchClick)) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        // Stitch: surface-container-low background
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    )
}
