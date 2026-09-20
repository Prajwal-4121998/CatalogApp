package com.example.catalogapp.feature.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalogapp.domain.product.GetProductsUseCase
import com.example.catalogapp.domain.product.ProductRepository
import com.example.catalogapp.domain.product.SyncResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val productRepository: ProductRepository
) : ViewModel() {

    // ─── State — single StateFlow, single source of truth ─────────────────────
    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    // ─── Effects — Channel for one-time events ─────────────────────────────────
    // Channel.BUFFERED ensures effects aren't dropped if UI isn't collecting yet
    private val _effect = Channel<CatalogEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        processIntent(CatalogIntent.LoadProducts)
    }

    // ─── Single entry point for ALL user actions ───────────────────────────────
    // This is the core MVI pattern — UI calls only this function
    fun processIntent(intent: CatalogIntent) {
        when (intent) {
            is CatalogIntent.LoadProducts -> loadProducts()
            is CatalogIntent.RefreshProducts -> refreshProducts()
            is CatalogIntent.RetryLoad -> loadProducts()
            is CatalogIntent.SearchClicked -> navigateToSearch()
            is CatalogIntent.SelectCategory -> selectCategory(intent.category)
            is CatalogIntent.ProductClicked -> navigateToDetail(intent.productId)
        }
    }

    // ─── Private handlers ──────────────────────────────────────────────────────
    private fun loadProducts() = fetchProducts(showAsRefresh = false)
    private fun refreshProducts() = fetchProducts(showAsRefresh = true)

    private fun fetchProducts(showAsRefresh: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                if (showAsRefresh) it.copy(isRefreshing = true) else it.copy(
                    isLoading = true,
                    error = null
                )
            }

            launch {
                getProductsUseCase().collect { products ->
                    _uiState.update { currentState ->
                        val categories = listOf("All") + products.map { it.category }.distinct().sorted()
                        currentState.copy(
                            products = products,
                            categories = categories,
                            filteredProducts = if (currentState.selectedCategory == "All") {
                                products
                            } else {
                                products.filter { it.category == currentState.selectedCategory }
                            }
                        )
                    }
                }
            }
            val result = if (showAsRefresh) {
                // Explicit pull-to-refresh always hits network, regardless of freshness
                productRepository.syncProducts()
            } else {
                // Initial load — only hits network if cache is actually stale
                productRepository.refreshIfStale()
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    hasCompletedInitialFetch = true,
                    error = if (result is SyncResult.Error) "Failed to load products" else null
                )
            }
        }
    }

    private fun selectCategory(category: String) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategory = category,
                filteredProducts = if (category == "All") {
                    currentState.products
                } else {
                    currentState.products.filter { it.category == category }
                }
            )
        }
    }

    private fun navigateToDetail(productId: Int) {
        viewModelScope.launch {
            _effect.send(CatalogEffect.NavigateToDetail(productId))
        }
    }

    private fun navigateToSearch() {
        viewModelScope.launch {
            _effect.send(CatalogEffect.NavigateToSearch)
        }
    }
}
