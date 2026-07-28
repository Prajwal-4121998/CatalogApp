package com.example.catalogapp.feature.search.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.domain.product.Product

@Composable
fun SearchResultsList(
    products: List<Product>,
    onProductClicked: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products, key = { it.id }) { product ->
            SearchResultCard(product = product, onClick = { onProductClicked(product.id) })
        }
    }
}

@Preview(showBackground = true, name = "ResultsList - multiple items")
@Composable
internal fun SearchResultsListPreview() {
    CatalogTheme {
        SearchResultsList(
            products = listOf(
                Product(
                    id = 1,
                    title = "Sleek Silver Drop Earrings",
                    price = 120.0,
                    description = "Minimalist silver drop earrings.",
                    category = "Jewelry",
                    imageUrl = "",
                    rating = 4.8f
                ),
                Product(
                    id = 2,
                    title = "Nitro Pulse Runner Pro",
                    price = 185.0,
                    description = "High-performance running shoes.",
                    category = "Footwear",
                    imageUrl = "",
                    rating = 4.9f
                ),
                Product(
                    id = 3,
                    title = "Aura Smartwatch Gen-4",
                    price = 349.99,
                    description = "Fitness-focused smartwatch.",
                    category = "Electronics",
                    imageUrl = "",
                    rating = 4.7f
                )
            ),
            onProductClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "ResultsList - single item")
@Composable
internal fun SearchResultsListSingleItemPreview() {
    CatalogTheme {
        SearchResultsList(
            products = listOf(
                Product(
                    id = 1,
                    title = "Sleek Silver Drop Earrings",
                    price = 120.0,
                    description = "Minimalist silver drop earrings.",
                    category = "Jewelry",
                    imageUrl = "",
                    rating = 4.8f
                )
            ),
            onProductClicked = {}
        )
    }
}
