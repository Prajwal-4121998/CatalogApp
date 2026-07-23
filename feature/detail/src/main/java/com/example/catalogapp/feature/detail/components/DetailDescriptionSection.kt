package com.example.catalogapp.feature.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.feature.detail.previewProduct

private const val COLLAPSED_MAX_LINES = 3

@Composable
internal fun DetailDescriptionSection(
    description: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = CatalogTheme.spacing
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.lg)
    ) {
        // Stitch: font-title-lg text-title-lg
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.sm))
        // Stitch: text-on-surface-variant font-body-lg line-clamp-3 when collapsed
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = if (isExpanded) Int.MAX_VALUE else COLLAPSED_MAX_LINES,
            overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis
        )
        // Stitch: text-primary font-label-lg "Read More / Read Less"
        TextButton(onClick = onToggle) {
            Text(
                text = if (isExpanded) "Read Less" else "Read More",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun DetailDescriptionPreview() {
    CatalogTheme {
        DetailDescriptionSection(
            description = previewProduct.description,
            isExpanded = false,
            onToggle = {}
        )
    }
}
