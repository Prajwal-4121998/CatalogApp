package com.example.catalogapp.feature.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.theme.CatalogTheme

private const val CHIP_CORNER_RADIUS_PERCENT = 50

@Composable
internal fun RecentSearchesSection(
    recentSearches: List<String>,
    onRecentClicked: (String) -> Unit,
    onClearAll: () -> Unit
) {
    if (recentSearches.isEmpty()) return

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT SEARCHES",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
            TextButton(onClick = onClearAll) {
                Text("Clear all")
            }
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            recentSearches.forEach { search ->
                RecentSearchChip(text = search, onClick = { onRecentClicked(search) })
            }
        }
    }
}

@Composable
private fun RecentSearchChip(text: String, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(text) },
        leadingIcon = { Icon(Icons.Default.History, contentDescription = null) },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(CHIP_CORNER_RADIUS_PERCENT),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    )
}

@Preview(showBackground = true, name = "RecentSearches - populated, wraps to 2 rows")
@Composable
internal fun RecentSearchesPopulatedPreview() {
    CatalogTheme {
        RecentSearchesSection(
            recentSearches = listOf(
                "Silver earrings", "Running shoes", "Smartwatch",
                "Leather jacket", "Wireless headphones"
            ),
            onRecentClicked = {},
            onClearAll = {}
        )
    }
}

@Preview(showBackground = true, name = "RecentSearches - single item")
@Composable
internal fun RecentSearchesSingleItemPreview() {
    CatalogTheme {
        RecentSearchesSection(
            recentSearches = listOf("Silver earrings"),
            onRecentClicked = {},
            onClearAll = {}
        )
    }
}

@Preview(showBackground = true, name = "RecentSearches - empty (renders nothing)")
@Composable
internal fun RecentSearchesEmptyPreview() {
    CatalogTheme {
        RecentSearchesSection(
            recentSearches = emptyList(),
            onRecentClicked = {},
            onClearAll = {}
        )
    }
}
