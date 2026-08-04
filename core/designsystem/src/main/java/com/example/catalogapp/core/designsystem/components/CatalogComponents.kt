package com.example.catalogapp.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.theme.CatalogTheme

private const val RETRY_BUTTON_WIDTH_FRACTION = 0.6f
private const val DEBOUNCE_INTERVAL_MS = 500L

@Composable
fun CatalogLoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
    }
}

@Composable
fun CatalogErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = CatalogTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.sm))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(spacing.lg))
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(RETRY_BUTTON_WIDTH_FRACTION)
        ) {
            Text(text = "Retry")
        }
    }
}

@Composable
fun CatalogEmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    val spacing = CatalogTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Nothing here yet",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.sm))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun rememberDebouncedOnClick(onClick: () -> Unit): () -> Unit {
    var lastClickTimeMs by remember { mutableLongStateOf(0L) }
    return {
        val now = System.currentTimeMillis()
        if (now - lastClickTimeMs >= DEBOUNCE_INTERVAL_MS) {
            lastClickTimeMs = now
            onClick()
        }
    }
}
