package com.example.catalogapp.feature.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.components.CatalogAsyncImage
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.domain.product.Product

private val CARD_CORNER_RADIUS = 12.dp
private val IMAGE_SIZE = 96.dp

@Composable
fun SearchResultCard(product: Product, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CARD_CORNER_RADIUS))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CatalogAsyncImage(
            imageUrl = product.imageUrl,
            contentDescription = product.title,
            modifier = Modifier
                .size(IMAGE_SIZE)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(modifier = Modifier.width(16.dp))
        SearchResultInfo(product)
        Icon(Icons.Default.ChevronRight, contentDescription = null)
    }
}

@Composable
private fun RowScope.SearchResultInfo(product: Product) {
    Column(modifier = Modifier.weight(1f)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = product.category.uppercase(LocalLocale.current.platformLocale),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = String.format(
                        LocalLocale.current.platformLocale,
                        "%.1f",
                        product.rating,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
        Text(
            text = product.title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text =
                String.format(LocalLocale.current.platformLocale, "$%.2f", product.price),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true, name = "ResultCard - long title")
@Composable
internal fun SearchResultCardLongTitlePreview() {
    CatalogTheme {
        SearchResultCard(
            product = Product(
                id = 1,
                title = "Extremely Long Product Title That Might Wrap To Two Lines",
                price = 999.99,
                description = "A product with a very long name to test text wrapping.",
                category = "Electronics",
                imageUrl = "",
                rating = 4.5f
            ),
            onClick = {}
        )
    }
}
