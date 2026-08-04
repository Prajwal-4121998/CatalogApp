package com.example.catalogapp.feature.catalog

import com.example.catalogapp.domain.product.GetProductsUseCase
import com.example.catalogapp.domain.product.Product
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getProductsUseCase: GetProductsUseCase = mockk()
    private lateinit var viewModel: CatalogViewModel

    private val fakeProducts = listOf(
        Product(1, "Backpack", 109.95, "Great bag", "men's clothing", "", 3.9f),
        Product(2, "Gold Ring", 4500.0, "22K gold", "jewelery", "", 4.5f)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state before loading has empty products and no error`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        viewModel = CatalogViewModel(getProductsUseCase)

        // Don't advance — check the default state before any coroutine runs
        val initialState = viewModel.uiState.value
        assertNull(initialState.error)
        // Either loading or empty initially — both are valid before coroutines run
        assertFalse(initialState.showError)
        assertFalse(initialState.showProducts)
    }

    @Test
    fun `LoadProducts intent loads products successfully`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        viewModel = CatalogViewModel(getProductsUseCase)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(2, state.products.size)
        assertTrue(state.showProducts)
        assertFalse(state.showLoading)
        assertFalse(state.showError)
        assertFalse(state.showEmpty)
    }

    @Test
    fun `SelectCategory intent filters products correctly`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        viewModel = CatalogViewModel(getProductsUseCase)
        advanceUntilIdle()

        viewModel.processIntent(CatalogIntent.SelectCategory("jewelery"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("jewelery", state.selectedCategory)
        assertEquals(1, state.filteredProducts.size)
        assertEquals("Gold Ring", state.filteredProducts[0].title)
    }

    @Test
    fun `SelectCategory All shows all products`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        viewModel = CatalogViewModel(getProductsUseCase)
        advanceUntilIdle()

        viewModel.processIntent(CatalogIntent.SelectCategory("jewelery"))
        viewModel.processIntent(CatalogIntent.SelectCategory("All"))
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.filteredProducts.size)
    }

    @Test
    fun `error from use case updates state correctly`() = runTest {
        every { getProductsUseCase() } returns kotlinx.coroutines.flow.flow {
            throw RuntimeException("Network error")
        }
        viewModel = CatalogViewModel(getProductsUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.showError)
        assertEquals("Network error", state.error)
        assertTrue(state.products.isEmpty())
    }

    @Test
    fun `categories derived from products include All plus unique categories`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        viewModel = CatalogViewModel(getProductsUseCase)
        advanceUntilIdle()

        val categories = viewModel.uiState.value.categories
        assertTrue(categories.contains("All"))
        assertTrue(categories.contains("men's clothing"))
        assertTrue(categories.contains("jewelery"))
        assertEquals(3, categories.size)
    }

    @Test
    fun `ProductClicked intent sends NavigateToDetail effect`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        viewModel = CatalogViewModel(getProductsUseCase)
        advanceUntilIdle()

        // Collect effect before sending intent
        var receivedEffect: CatalogEffect? = null
        val job = launch {
            viewModel.effect.collect { receivedEffect = it }
        }

        viewModel.processIntent(CatalogIntent.ProductClicked(productId = 1))
        advanceUntilIdle()
        job.cancel()

        assertTrue(receivedEffect is CatalogEffect.NavigateToDetail)
        assertEquals(1, (receivedEffect as CatalogEffect.NavigateToDetail).productId)
    }

    @Test
    fun `RefreshProducts intent re-fetches products via use case`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        viewModel = CatalogViewModel(getProductsUseCase)
        advanceUntilIdle()

        viewModel.processIntent(CatalogIntent.RefreshProducts)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isRefreshing)
        assertEquals(2, state.products.size)
        // getProductsUseCase() should fire once on init, once on refresh —
        // this is what actually distinguishes a real refresh from the old no-op
        verify(exactly = 2) { getProductsUseCase() }
    }

    @Test
    fun `RefreshProducts intent surfaces error without wiping existing products`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        viewModel = CatalogViewModel(getProductsUseCase)
        advanceUntilIdle()

        every { getProductsUseCase() } returns kotlinx.coroutines.flow.flow {
            throw RuntimeException("Refresh failed")
        }
        viewModel.processIntent(CatalogIntent.RefreshProducts)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isRefreshing)
        assertEquals("Refresh failed", state.error)
        // Products from the successful initial load must survive a failed refresh
        assertEquals(2, state.products.size)
    }
    @Test
    fun `SearchClicked intent sends NavigateToSearch effect`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        viewModel = CatalogViewModel(getProductsUseCase)
        advanceUntilIdle()

        viewModel.processIntent(CatalogIntent.SearchClicked)
        advanceUntilIdle()

        var receivedEffect: CatalogEffect? = null
        val job = launch {
            viewModel.effect.collect { receivedEffect = it }
        }
        advanceUntilIdle()
        job.cancel()

        assertTrue(receivedEffect is CatalogEffect.NavigateToSearch)
    }
}
