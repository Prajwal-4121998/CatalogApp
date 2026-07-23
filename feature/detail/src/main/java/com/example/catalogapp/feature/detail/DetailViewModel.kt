package com.example.catalogapp.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalogapp.domain.product.ProductRepository
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
class DetailViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _effect = Channel<DetailEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun processIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.LoadProduct -> loadProduct(intent.productId)
            is DetailIntent.NavigateBack -> navigateBack()
            is DetailIntent.ToggleWishlist -> toggleWishlist()
            is DetailIntent.AddToCart -> addToCart()
            is DetailIntent.ToggleDescription -> toggleDescription()
            is DetailIntent.ShareProduct -> shareProduct()
        }
    }

    private fun loadProduct(productId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val product = productRepository.getProductById(productId)
            if (product != null) {
                _uiState.update { it.copy(isLoading = false, product = product) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Product not found"
                    )
                }
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _effect.send(DetailEffect.NavigateBack)
        }
    }

    private fun toggleWishlist() {
        _uiState.update { it.copy(isWishlisted = !it.isWishlisted) }
        viewModelScope.launch {
            val message = if (_uiState.value.isWishlisted) {
                "Added to wishlist"
            } else {
                "Removed from wishlist"
            }
            _effect.send(DetailEffect.ShowSnackbar(message))
        }
    }

    private fun addToCart() {
        viewModelScope.launch {
            _effect.send(DetailEffect.ShowSnackbar("Added to cart"))
        }
    }

    private fun toggleDescription() {
        _uiState.update { it.copy(isDescriptionExpanded = !it.isDescriptionExpanded) }
    }

    private fun shareProduct() {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            _effect.send(DetailEffect.ShareProduct(product.title, product.price))
        }
    }
}
