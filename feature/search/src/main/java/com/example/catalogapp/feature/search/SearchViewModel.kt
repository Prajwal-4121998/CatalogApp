package com.example.catalogapp.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalogapp.domain.product.GetProductsUseCase
import com.example.catalogapp.domain.product.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DEBOUNCE_MILLIS = 300L
private const val MAX_RECENT_SEARCHES = 5
private const val LOAD_ERROR_MESSAGE =
    "Couldn't load products. Check your connection and try again."

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _effect = Channel<SearchEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        loadCachedProducts()
        observeQueryChanges()
    }

    fun processIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> onQueryChanged(intent.query)
            is SearchIntent.ClearQuery -> onQueryChanged("")
            is SearchIntent.ProductClicked -> onProductClicked(intent.productId)
            is SearchIntent.RecentSearchClicked -> onQueryChanged(intent.query)
            is SearchIntent.ClearRecentSearches -> clearRecentSearches()
            is SearchIntent.RetryLoad -> loadCachedProducts()
        }
    }

    private fun loadCachedProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getProductsUseCase()
                .catch { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, error = exception.message ?: LOAD_ERROR_MESSAGE)
                    }
                }
                .collect { products ->
                    _uiState.update {
                        it.copy(
                            allProducts = products,
                            availableCategories = products.map { p -> p.category }.distinct(),
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeQueryChanges() {
        viewModelScope.launch {
            queryFlow
                .debounce(DEBOUNCE_MILLIS)
                .distinctUntilChanged()
                .collect { query -> filterProducts(query) }
        }
    }

    private fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        queryFlow.value = query
    }

    private fun filterProducts(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(filteredProducts = emptyList(), hasSearched = false) }
            return
        }
        val currentState = _uiState.value
        val matches = searchProductsUseCase(currentState.allProducts, query)
        _uiState.update { it.copy(filteredProducts = matches, hasSearched = true) }
        if (matches.isNotEmpty()) addRecentSearch(query)
    }

    private fun addRecentSearch(query: String) {
        val updated = listOf(query) + _uiState.value.recentSearches.filterNot { it == query }
        _uiState.update { it.copy(recentSearches = updated.take(MAX_RECENT_SEARCHES)) }
    }

    private fun clearRecentSearches() {
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    private fun onProductClicked(productId: Int) {
        viewModelScope.launch {
            _effect.send(SearchEffect.DismissKeyboard)
            _effect.send(SearchEffect.NavigateToDetail(productId))
        }
    }
}

