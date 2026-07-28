package com.example.catalogapp.feature.search

import com.example.catalogapp.domain.product.GetProductsUseCase
import com.example.catalogapp.domain.product.Product
import com.example.catalogapp.domain.product.SearchProductsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var searchProductsUseCase: SearchProductsUseCase
    private lateinit var viewModel: SearchViewModel

    private val sampleProducts = listOf(
        Product(
            id = 1, title = "Silver Earrings", price = 120.0,
            description = "d", category = "Jewelry", imageUrl = "", rating = 4.8f
        ),
        Product(
            id = 2, title = "Running Shoes", price = 185.0,
            description = "d", category = "Footwear", imageUrl = "", rating = 4.9f
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getProductsUseCase = mockk()
        searchProductsUseCase = mockk()
        coEvery { getProductsUseCase() } returns flowOf(sampleProducts)
        viewModel = SearchViewModel(getProductsUseCase, searchProductsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Loading ---

    @Test
    fun `cached products load successfully on init`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.allProducts.size)
        assertTrue(!state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `available categories are derived and deduplicated from loaded products`() =
        runTest(testDispatcher) {
            testDispatcher.scheduler.advanceUntilIdle()

            val categories = viewModel.uiState.value.availableCategories
            assertEquals(2, categories.size)
            assertTrue(categories.contains("Jewelry"))
            assertTrue(categories.contains("Footwear"))
        }

    // --- Debounce — proves timing, delegates matching to the mocked use case ---

    @Test
    fun `debounce - use case only invoked after 300ms of no new input`() = runTest(testDispatcher) {
        every { searchProductsUseCase(any(), any()) } returns listOf(sampleProducts[0])
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(SearchIntent.QueryChanged("s"))
        testDispatcher.scheduler.advanceTimeBy(100)
        viewModel.processIntent(SearchIntent.QueryChanged("si"))
        testDispatcher.scheduler.advanceTimeBy(100)
        viewModel.processIntent(SearchIntent.QueryChanged("silver"))

        assertTrue(viewModel.uiState.value.filteredProducts.isEmpty())

        testDispatcher.scheduler.advanceTimeBy(301)
        assertEquals(1, viewModel.uiState.value.filteredProducts.size)
    }

    @Test
    fun `rapid clear then retype does not leak a stale filter result`() = runTest(testDispatcher) {
        every { searchProductsUseCase(any(), "shoes") } returns listOf(sampleProducts[1])
        every { searchProductsUseCase(any(), "silver") } returns listOf(sampleProducts[0])
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(SearchIntent.QueryChanged("shoes"))
        testDispatcher.scheduler.advanceTimeBy(50)
        viewModel.processIntent(SearchIntent.ClearQuery)
        testDispatcher.scheduler.advanceTimeBy(50)
        viewModel.processIntent(SearchIntent.QueryChanged("silver"))
        testDispatcher.scheduler.advanceTimeBy(301)

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredProducts.size)
        assertEquals("Silver Earrings", state.filteredProducts.first().title)
    }

    // --- State transitions around the use case boundary ---

    @Test
    fun `empty query resets filtered results and hasSearched without calling use case`() =
        runTest(testDispatcher) {
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.processIntent(SearchIntent.ClearQuery)
            testDispatcher.scheduler.advanceTimeBy(301)

            val state = viewModel.uiState.value
            assertTrue(state.filteredProducts.isEmpty())
            assertTrue(!state.hasSearched)
        }

    @Test
    fun `no matches from use case sets hasSearched true with empty results`() =
        runTest(testDispatcher) {
            every { searchProductsUseCase(any(), any()) } returns emptyList()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.processIntent(SearchIntent.QueryChanged("nonexistent"))
            testDispatcher.scheduler.advanceTimeBy(301)

            val state = viewModel.uiState.value
            assertTrue(state.filteredProducts.isEmpty())
            assertTrue(state.hasSearched)
        }

    // --- Recent searches ---

    @Test
    fun `successful match adds query to recent searches`() = runTest(testDispatcher) {
        every { searchProductsUseCase(any(), any()) } returns listOf(sampleProducts[1])
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(SearchIntent.QueryChanged("shoes"))
        testDispatcher.scheduler.advanceTimeBy(301)

        assertTrue(viewModel.uiState.value.recentSearches.contains("shoes"))
    }

    @Test
    fun `query with no matches is not added to recent searches`() = runTest(testDispatcher) {
        every { searchProductsUseCase(any(), any()) } returns emptyList()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(SearchIntent.QueryChanged("nonexistent"))
        testDispatcher.scheduler.advanceTimeBy(301)

        assertTrue(viewModel.uiState.value.recentSearches.isEmpty())
    }

    @Test
    fun `duplicate recent search moves to front instead of duplicating`() =
        runTest(testDispatcher) {
            every { searchProductsUseCase(any(), "shoes") } returns listOf(sampleProducts[1])
            every { searchProductsUseCase(any(), "silver") } returns listOf(sampleProducts[0])
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.processIntent(SearchIntent.QueryChanged("shoes"))
            testDispatcher.scheduler.advanceTimeBy(301)
            viewModel.processIntent(SearchIntent.QueryChanged("silver"))
            testDispatcher.scheduler.advanceTimeBy(301)
            viewModel.processIntent(SearchIntent.QueryChanged("shoes"))
            testDispatcher.scheduler.advanceTimeBy(301)

            val recents = viewModel.uiState.value.recentSearches
            assertEquals("shoes", recents.first())
            assertEquals(1, recents.count { it == "shoes" })
        }

    @Test
    fun `clear recent searches empties the list`() = runTest(testDispatcher) {
        every { searchProductsUseCase(any(), any()) } returns listOf(sampleProducts[1])
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(SearchIntent.QueryChanged("shoes"))
        testDispatcher.scheduler.advanceTimeBy(301)
        viewModel.processIntent(SearchIntent.ClearRecentSearches)

        assertTrue(viewModel.uiState.value.recentSearches.isEmpty())
    }

    @Test
    fun `recent search click re-triggers filter for that query`() = runTest(testDispatcher) {
        every { searchProductsUseCase(any(), "silver") } returns listOf(sampleProducts[0])
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(SearchIntent.RecentSearchClicked("silver"))
        testDispatcher.scheduler.advanceTimeBy(301)

        val state = viewModel.uiState.value
        assertEquals("silver", state.query)
        assertEquals(1, state.filteredProducts.size)
    }

    // --- Effects ---

    @Test
    fun `product click emits DismissKeyboard then NavigateToDetail effects`() =
        runTest(testDispatcher) {
            val effects = mutableListOf<SearchEffect>()
            val job = launch { viewModel.effect.collect { effects.add(it) } }

            viewModel.processIntent(SearchIntent.ProductClicked(1))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(SearchEffect.DismissKeyboard, effects[0])
            assertEquals(SearchEffect.NavigateToDetail(1), effects[1])
            job.cancel()
        }

    // --- Error / retry ---

    @Test
    fun `load failure sets error state and clears loading`() = runTest(testDispatcher) {
        coEvery { getProductsUseCase() } returns flow { throw Exception("network down") }
        val vm = SearchViewModel(getProductsUseCase, searchProductsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state.error != null)
        assertTrue(!state.isLoading)
    }

    @Test
    fun `retry after failure reloads products successfully`() = runTest(testDispatcher) {
        coEvery { getProductsUseCase() } returns flow { throw Exception("network down") }
        val vm = SearchViewModel(getProductsUseCase, searchProductsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.uiState.value.error != null)

        coEvery { getProductsUseCase() } returns flowOf(sampleProducts)
        vm.processIntent(SearchIntent.RetryLoad)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertNull(state.error)
        assertEquals(2, state.allProducts.size)
    }
}

