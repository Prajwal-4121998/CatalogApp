package com.example.catalogapp.feature.catalog

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.domain.product.Product
import com.example.catalogapp.feature.catalog.components.CatalogBody
import com.example.catalogapp.feature.catalog.components.CatalogBottomBar
import com.example.catalogapp.feature.catalog.components.CatalogFab
import com.example.catalogapp.feature.catalog.components.CatalogTopBar

// ─── Top-level screen — owns ViewModel, handles effects ───────────────────────
@Composable
fun CatalogScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CatalogEffect.NavigateToDetail -> onNavigateToDetail(effect.productId)
                is CatalogEffect.NavigateToSearch -> { }
                is CatalogEffect.ShowSnackbar -> { }
            }
        }
    }
    CatalogContent(
        uiState = uiState,
        onIntent = viewModel::processIntent
    )
}

// ─── Stateless content composable — thin composition root ────────────────────
// Scaffold slots (top bar, bottom bar, fab, body) each live in their own file
// under components/, so this stays small and easy to scan (TooManyFunctions /
// LongMethod safe by construction rather than by suppression).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CatalogContent(
    uiState: CatalogUiState,
    onIntent: (CatalogIntent) -> Unit
) {
    var selectedNavItem by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CatalogTopBar(onSearchClick = { onIntent(CatalogIntent.SearchClicked) })
        },
        bottomBar = {
            CatalogBottomBar(
                selectedIndex = selectedNavItem,
                onItemSelected = { selectedNavItem = it }
            )
        },
        floatingActionButton = {
            CatalogFab(onClick = { })
        }
    ) { paddingValues ->
        CatalogBody(
            uiState = uiState,
            onIntent = onIntent,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────
@Preview(showBackground = true, name = "Catalog - Products")
@Composable
internal fun CatalogContentPreview() {
    CatalogTheme {
        CatalogContent(
            uiState = CatalogUiState(
                products = previewProducts,
                selectedCategory = "All"
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Catalog - Loading")
@Composable
internal fun CatalogLoadingPreview() {
    CatalogTheme {
        CatalogContent(
            uiState = CatalogUiState(isLoading = true),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Catalog - Error")
@Composable
internal fun CatalogErrorPreview() {
    CatalogTheme {
        CatalogContent(
            uiState = CatalogUiState(
                error = "Unable to load products."
            ),
            onIntent = {}
        )
    }
}

private val previewProducts = listOf(
    Product(1, "Premium Stealth Titanium Smartwatch Series 9", 24999.0, "Great watch", "electronics", "", 4.8f),
    Product(2, "Linen Heritage Slim-Fit Kurta - Emerald", 3450.0, "Great kurta", "men's clothing", "", 4.6f),
    Product(3, "SonicFlow Noise-Cancelling Headphones", 18200.0, "Great headphones", "electronics", "", 4.9f),
    Product(4, "Geometric Prism Gold Earrings", 12499.0, "Great earrings", "jewelery", "", 4.5f)
)
