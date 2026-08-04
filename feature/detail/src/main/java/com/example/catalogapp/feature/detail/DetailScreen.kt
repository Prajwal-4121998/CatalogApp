package com.example.catalogapp.feature.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.catalogapp.core.designsystem.components.CatalogErrorState
import com.example.catalogapp.core.designsystem.components.DetailSkeleton
import com.example.catalogapp.core.designsystem.theme.CatalogTheme
import com.example.catalogapp.domain.product.Product
import com.example.catalogapp.feature.detail.components.DetailBody
import com.example.catalogapp.feature.detail.components.DetailBottomBar
import com.example.catalogapp.feature.detail.components.DetailTopBar

@Composable
fun DetailScreen(
    productId: Int,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(productId) {
        viewModel.processIntent(DetailIntent.LoadProduct(productId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DetailEffect.NavigateBack -> onNavigateBack()
                is DetailEffect.ShowSnackbar ->
                    snackbarHostState.showSnackbar(effect.message)

                is DetailEffect.ShareProduct -> {}
            }
        }
    }

    DetailContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::processIntent
    )
}

@Composable
internal fun DetailContent(
    uiState: DetailUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onIntent: (DetailIntent) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.showContent) {
                DetailBottomBar(
                    isWishlisted = uiState.isWishlisted,
                    onWishlistClick = { onIntent(DetailIntent.ToggleWishlist) },
                    onAddToCartClick = { onIntent(DetailIntent.AddToCart) }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.showLoading -> DetailSkeleton(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding())
                )
                uiState.showError -> CatalogErrorState(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { onIntent(DetailIntent.RetryLoad) }
                )

                uiState.showContent -> {
                    val product = uiState.product!!
                    DetailBody(
                        product = product,
                        uiState = uiState,
                        onIntent = onIntent,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = paddingValues.calculateBottomPadding())
                            .verticalScroll(scrollState)
                    )
                }
            }
            // Top bar always on top regardless of content state
            DetailTopBar(
                onBackClick = { onIntent(DetailIntent.NavigateBack) },
                onShareClick = { onIntent(DetailIntent.ShareProduct) }
            )
        }
    }
}

@Preview(showBackground = true, name = "Detail - Content")
@Composable
internal fun DetailContentPreview() {
    CatalogTheme {
        DetailContent(
            uiState = DetailUiState(product = previewProduct),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail - Loading")
@Composable
internal fun DetailLoadingPreview() {
    CatalogTheme {
        DetailContent(
            uiState = DetailUiState(isLoading = true),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail - Error")
@Composable
internal fun DetailErrorPreview() {
    CatalogTheme {
        DetailContent(
            uiState = DetailUiState(error = "Product not found"),
            onIntent = {}
        )
    }
}

internal val previewProduct = Product(
    id = 1,
    title = "Titan Celestial Smartwatch - Midnight Edition",
    price = 12999.0,
    description = "Experience precision and luxury with the Celestial Midnight Edition. Features a brilliant 1.43\"" +
            " AMOLED display with 1000 nits brightness.",
    category = "electronics",
    imageUrl = "",
    rating = 4.8f
)
