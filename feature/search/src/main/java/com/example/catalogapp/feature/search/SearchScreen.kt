package com.example.catalogapp.feature.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.catalogapp.core.designsystem.components.CatalogEmptyState
import com.example.catalogapp.core.designsystem.components.CatalogLoadingState
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.domain.product.Product
import com.example.catalogapp.feature.search.components.RecentSearchesSection
import com.example.catalogapp.feature.search.components.SearchBar
import com.example.catalogapp.feature.search.components.SearchErrorState
import com.example.catalogapp.feature.search.components.SearchPromptState
import com.example.catalogapp.feature.search.components.SearchResultsList

@Composable
fun SearchScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchEffect.NavigateToDetail -> onNavigateToDetail(effect.productId)
                is SearchEffect.DismissKeyboard -> keyboardController?.hide()
            }
        }
    }

    SearchContent(
        uiState = uiState,
        onIntent = viewModel::processIntent
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchContent(
    uiState: SearchUiState,
    onIntent: (SearchIntent) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SearchBar(
                    query = uiState.query,
                    onQueryChanged = { onIntent(SearchIntent.QueryChanged(it)) },
                    onClear = { onIntent(SearchIntent.ClearQuery) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        ) {
            when {
                uiState.error != null -> SearchErrorState(
                    message = uiState.error,
                    onRetry = { onIntent(SearchIntent.RetryLoad) }
                )

                uiState.isLoading -> CatalogLoadingState()
                uiState.query.isBlank() -> {
                    if (uiState.recentSearches.isNotEmpty()) {
                        RecentSearchesSection(
                            recentSearches = uiState.recentSearches,
                            onRecentClicked = { onIntent(SearchIntent.RecentSearchClicked(it)) },
                            onClearAll = { onIntent(SearchIntent.ClearRecentSearches) }
                        )
                    } else {
                        SearchPromptState(
                            categories = uiState.availableCategories,
                            onCategoryClicked = { onIntent(SearchIntent.RecentSearchClicked(it)) }
                        )
                    }
                }

                uiState.hasSearched && uiState.filteredProducts.isEmpty() -> CatalogEmptyState(
                    message = "No products found."
                )

                else -> SearchResultsList(
                    products = uiState.filteredProducts,
                    onProductClicked = { onIntent(SearchIntent.ProductClicked(it)) }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Content - error")
@Composable
internal fun SearchContentErrorPreview() {
    CatalogTheme {
        SearchContent(
            uiState = SearchUiState(error = "Couldn't load products. Try again."),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - loading")
@Composable
internal fun SearchContentLoadingPreview() {
    CatalogTheme {
        SearchContent(
            uiState = SearchUiState(isLoading = true),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - empty query, no recents")
@Composable
internal fun SearchContentEmptyQueryNoRecentsPreview() {
    CatalogTheme {
        SearchContent(
            uiState = SearchUiState(),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - empty query, with recents")
@Composable
internal fun SearchContentWithRecentsPreview() {
    CatalogTheme {
        SearchContent(
            uiState = SearchUiState(
                recentSearches = listOf("Silver earrings", "Running shoes")
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - no results found")
@Composable
internal fun SearchContentNoResultsPreview() {
    CatalogTheme {
        SearchContent(
            uiState = SearchUiState(
                query = "xyz123",
                hasSearched = true,
                filteredProducts = emptyList()
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - results found")
@Composable
internal fun SearchContentWithResultsPreview() {
    CatalogTheme {
        SearchContent(
            uiState = SearchUiState(
                query = "silver",
                hasSearched = true,
                filteredProducts = listOf(
                    Product(
                        id = 1,
                        title = "Sleek Silver Drop Earrings",
                        price = 120.0,
                        description = "Minimalist silver drop earrings.",
                        category = "Jewelry",
                        imageUrl = "",
                        rating = 4.8f
                    )
                )
            ),
            onIntent = {}
        )
    }
}
