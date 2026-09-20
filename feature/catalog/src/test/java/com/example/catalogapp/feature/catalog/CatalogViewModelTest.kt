package com.example.catalogapp.feature.catalog

import com.example.catalogapp.domain.product.GetProductsUseCase
import com.example.catalogapp.domain.product.Product
import com.example.catalogapp.domain.product.ProductRepository
import com.example.catalogapp.domain.product.SyncError
import com.example.catalogapp.domain.product.SyncResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getProductsUseCase: GetProductsUseCase = mockk()
    private val productRepository: ProductRepository = mockk()
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

    private fun createViewModel() = CatalogViewModel(getProductsUseCase, productRepository)

    // ─── Default stub for the LoadProducts path used by most tests ────────────
    // LoadProducts now calls refreshIfStale(), not syncProducts() — every test
    // that only cares about the "happy path" load should stub refreshIfStale().
    private fun stubFreshCacheLoad() {
        coEvery { productRepository.refreshIfStale() } returns null
    }

    @Test
    fun `initial state before loading has empty products and no error`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        stubFreshCacheLoad()
        viewModel = createViewModel()

        val initialState = viewModel.uiState.value
        assertNull(initialState.error)
        assertFalse(initialState.showError)
        assertFalse(initialState.showProducts)
    }

    @Test
    fun `LoadProducts intent loads products and marks initial fetch complete`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        stubFreshCacheLoad()
        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(2, state.products.size)
        assertTrue(state.showProducts)
        assertFalse(state.showLoading)
        assertFalse(state.showError)
        assertFalse(state.showEmpty)
        assertTrue(state.hasCompletedInitialFetch)
    }

    @Test
    fun `showEmpty only becomes true after refreshIfStale completes with genuinely empty results`() =
        runTest {
            every { getProductsUseCase() } returns flowOf(emptyList())
            coEvery { productRepository.refreshIfStale() } returns SyncResult.Success
            viewModel = createViewModel()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state.hasCompletedInitialFetch)
            assertTrue(state.showEmpty)
            assertFalse(state.showLoading)
        }

    @Test
    fun `showLoading stays true while refreshIfStale has not yet completed even with empty cache`() =
        runTest {
            every { getProductsUseCase() } returns flowOf(emptyList())
            coEvery { productRepository.refreshIfStale() } coAnswers {
                delay(Long.MAX_VALUE.milliseconds) // never resolves in this test
                SyncResult.Success
            }
            viewModel = createViewModel()
            testDispatcher.scheduler.advanceTimeBy(10)

            val state = viewModel.uiState.value
            assertFalse(state.hasCompletedInitialFetch)
            assertTrue(state.showLoading)
            assertFalse(state.showEmpty)
        }

    @Test
    fun `SelectCategory intent filters products correctly`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        stubFreshCacheLoad()
        viewModel = createViewModel()
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
        stubFreshCacheLoad()
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(CatalogIntent.SelectCategory("jewelery"))
        viewModel.processIntent(CatalogIntent.SelectCategory("All"))
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.filteredProducts.size)
    }

    @Test
    fun `refreshIfStale error surfaces in state without wiping cached products`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        coEvery { productRepository.refreshIfStale() } returns
                SyncResult.Error(SyncError.NoInternet)
        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.hasCompletedInitialFetch)
        assertEquals(2, state.products.size)
        assertTrue(state.showProducts)
    }

    @Test
    fun `categories derived from products include All plus unique categories`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        stubFreshCacheLoad()
        viewModel = createViewModel()
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
        stubFreshCacheLoad()
        viewModel = createViewModel()
        advanceUntilIdle()

        var receivedEffect: CatalogEffect? = null
        val job = launch { viewModel.effect.collect { receivedEffect = it } }

        viewModel.processIntent(CatalogIntent.ProductClicked(productId = 1))
        advanceUntilIdle()
        job.cancel()

        assertTrue(receivedEffect is CatalogEffect.NavigateToDetail)
        assertEquals(1, (receivedEffect as CatalogEffect.NavigateToDetail).productId)
    }

    @Test
    fun `RefreshProducts intent always calls syncProducts, never refreshIfStale`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        stubFreshCacheLoad()
        coEvery { productRepository.syncProducts() } returns SyncResult.Success
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(CatalogIntent.RefreshProducts)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isRefreshing)
        assertEquals(2, state.products.size)
        // refreshIfStale fires once during initial LoadProducts (via init{})
        coVerify(exactly = 1) { productRepository.refreshIfStale() }
        // syncProducts fires once, only during the explicit RefreshProducts call
        coVerify(exactly = 1) { productRepository.syncProducts() }
    }

    @Test
    fun `RefreshProducts surfaces error without wiping existing products`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        stubFreshCacheLoad()
        viewModel = createViewModel()
        advanceUntilIdle()

        coEvery { productRepository.syncProducts() } returns
                SyncResult.Error(SyncError.Timeout)
        viewModel.processIntent(CatalogIntent.RefreshProducts)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isRefreshing)
        assertEquals("Failed to load products", state.error)
        assertEquals(2, state.products.size)
    }

    @Test
    fun `SearchClicked intent sends NavigateToSearch effect`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        stubFreshCacheLoad()
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(CatalogIntent.SearchClicked)

        var receivedEffect: CatalogEffect? = null
        val job = launch { viewModel.effect.collect { receivedEffect = it } }
        advanceUntilIdle()
        job.cancel()

        assertTrue(receivedEffect is CatalogEffect.NavigateToSearch)
    }

    @Test
    fun `LoadProducts with fresh cache never calls syncProducts, only refreshIfStale`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        stubFreshCacheLoad()
        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.hasCompletedInitialFetch)
        assertFalse(state.isLoading)
        assertNull(state.error)
        coVerify(exactly = 1) { productRepository.refreshIfStale() }
        coVerify(exactly = 0) { productRepository.syncProducts() }
    }

    @Test
    fun `LoadProducts with stale cache calls refreshIfStale and surfaces its result`() = runTest {
        every { getProductsUseCase() } returns flowOf(fakeProducts)
        coEvery { productRepository.refreshIfStale() } returns SyncResult.Error(SyncError.Timeout)
        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.hasCompletedInitialFetch)
        assertEquals("Failed to load products", state.error)
        assertEquals(2, state.products.size)
    }
}