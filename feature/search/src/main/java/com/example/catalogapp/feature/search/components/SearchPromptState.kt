package com.example.catalogapp.feature.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.components.rememberDebouncedOnClick
import com.example.catalogapp.core.designsystem.theme.CatalogTheme

private val PROMPT_TOP_PADDING = 48.dp
private val PROMPT_ICON_SIZE = 64.dp

@Composable
internal fun SearchPromptState(
    categories: List<String>,
    onCategoryClicked: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = PROMPT_TOP_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(PROMPT_ICON_SIZE)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Search for products",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Try a name, or browse a category below",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { category ->
                AssistChip(
                    onClick = rememberDebouncedOnClick { onCategoryClicked(category) },
                    label = { Text(category) }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "SearchPromptState - with categories")
@Composable
internal fun SearchPromptStatePreview() {
    CatalogTheme {
        SearchPromptState(
            categories = listOf("Electronics", "Jewelry", "Footwear", "Men's Clothing"),
            onCategoryClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "SearchPromptState - single category")
@Composable
internal fun SearchPromptStateSingleCategoryPreview() {
    CatalogTheme {
        SearchPromptState(
            categories = listOf("Electronics"),
            onCategoryClicked = {}
        )
    }
}
