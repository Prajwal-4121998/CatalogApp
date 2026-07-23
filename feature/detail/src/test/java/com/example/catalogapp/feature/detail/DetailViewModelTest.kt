package com.example.catalogapp.feature.detail

import com.example.catalogapp.domain.product.Product
import com.example.catalogapp.domain.product.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val productRepository: ProductRepository = mockk()
    private lateinit var viewModel: DetailViewModel

    private val fakeProduct = Product(
        id = 1,
        title = "Test Product",
        price = 999.0,
        description = "Test description",
        category = "electronics",
        imageUrl = "https://example.com/image.png",
        rating = 4.5f
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DetailViewModel(productRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.uiState.value
        assertNull(state.product)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertFalse(state.isWishlisted)
        assertFalse(state.isDescriptionExpanded)
    }

    @Test
    fun `LoadProduct sets loading then shows product on success`() = runTest {
        coEvery { productRepository.getProductById(1) } returns fakeProduct

        viewModel.processIntent(DetailIntent.LoadProduct(1))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.product)
        assertEquals("Test Product", state.product?.title)
        assertTrue(state.showContent)
        assertFalse(state.showLoading)
        assertFalse(state.showError)
    }

    @Test
    fun `LoadProduct sets error when product not found`() = runTest {
        coEvery { productRepository.getProductById(99) } returns null

        viewModel.processIntent(DetailIntent.LoadProduct(99))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.product)
        assertEquals("Product not found", state.error)
        assertTrue(state.showError)
    }

    @Test
    fun `ToggleWishlist toggles isWishlisted state`() = runTest {
        coEvery { productRepository.getProductById(1) } returns fakeProduct
        viewModel.processIntent(DetailIntent.LoadProduct(1))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isWishlisted)
        viewModel.processIntent(DetailIntent.ToggleWishlist)
        assertTrue(viewModel.uiState.value.isWishlisted)
        viewModel.processIntent(DetailIntent.ToggleWishlist)
        assertFalse(viewModel.uiState.value.isWishlisted)
    }

    @Test
    fun `ToggleDescription toggles isDescriptionExpanded state`() = runTest {
        assertFalse(viewModel.uiState.value.isDescriptionExpanded)
        viewModel.processIntent(DetailIntent.ToggleDescription)
        assertTrue(viewModel.uiState.value.isDescriptionExpanded)
        viewModel.processIntent(DetailIntent.ToggleDescription)
        assertFalse(viewModel.uiState.value.isDescriptionExpanded)
    }

    @Test
    fun `NavigateBack sends NavigateBack effect`() = runTest {
        viewModel.processIntent(DetailIntent.NavigateBack)
        advanceUntilIdle()

        var receivedEffect: DetailEffect? = null
        val job = launch {
            viewModel.effect.collect { receivedEffect = it }
        }
        advanceUntilIdle()
        job.cancel()

        assertTrue(receivedEffect is DetailEffect.NavigateBack)
    }

    @Test
    fun `AddToCart sends ShowSnackbar effect with correct message`() = runTest {
        viewModel.processIntent(DetailIntent.AddToCart)
        advanceUntilIdle()

        var receivedEffect: DetailEffect? = null
        val job = launch {
            viewModel.effect.collect { receivedEffect = it }
        }
        advanceUntilIdle()
        job.cancel()

        assertTrue(receivedEffect is DetailEffect.ShowSnackbar)
        assertEquals("Added to cart", (receivedEffect as DetailEffect.ShowSnackbar).message)
    }
}
