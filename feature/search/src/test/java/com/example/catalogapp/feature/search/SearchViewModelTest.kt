package com.example.catalogapp.feature.search

import com.example.catalogapp.domain.product.GetProductsUseCase
import com.example.catalogapp.domain.product.Product
import io.mockk.coEvery
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
    private lateinit var viewModel: SearchViewModel

    private val sampleProducts = listOf(
        Product(
            id = 1,
            title = "Silver Earrings",
            price = 120.0,
            description = "Elegant silver earrings.",
            category = "Jewelry",
            imageUrl = "",
            rating = 4.8f
        ),
        Product(
            id = 2,
            title = "Running Shoes",
            price = 185.0,
            description = "Performance running shoes.",
            category = "Footwear",
            imageUrl = "",
            rating = 4.9f
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getProductsUseCase = mockk()
        coEvery { getProductsUseCase() } returns flowOf(sampleProducts)
        viewModel = SearchViewModel(getProductsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Loading / initial state ---

    @Test
    fun `cached products load successfully on init`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.allProducts.size)
        assertTrue(!state.isLoading)
        assertNull(state.error)
    }

    // --- Debounce behavior — the core pattern this session is about ---

    @Test
    fun `debounce - filter only runs after 300ms of no new input`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(SearchIntent.QueryChanged("s"))
        testDispatcher.scheduler.advanceTimeBy(100)
        viewModel.processIntent(SearchIntent.QueryChanged("si"))
        testDispatcher.scheduler.advanceTimeBy(100)
        viewModel.processIntent(SearchIntent.QueryChanged("silver"))

        // still within the debounce window — no filter should have run yet
        assertTrue(viewModel.uiState.value.filteredProducts.isEmpty())

        testDispatcher.scheduler.advanceTimeBy(301)
        assertEquals(1, viewModel.uiState.value.filteredProducts.size)
        assertEquals("Silver Earrings", viewModel.uiState.value.filteredProducts.first().title)
    }

    @Test
    fun `rapid clear then retype does not leak a stale filter result`() = runTest(testDispatcher) {
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

    // --- Filter correctness ---

    @Test
    fun `filter matches by category as well as title`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(SearchIntent.QueryChanged("footwear"))
        testDispatcher.scheduler.advanceTimeBy(301)

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredProducts.size)
        assertEquals("Running Shoes", state.filteredProducts.first().title)
    }

    @Test
    fun `filter is case-insensitive`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(SearchIntent.QueryChanged("SILVER"))
        testDispatcher.scheduler.advanceTimeBy(301)

        assertEquals(1, viewModel.uiState.value.filteredProducts.size)
    }

    @Test
    fun `empty query resets filtered results and hasSearched`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.processIntent(SearchIntent.QueryChanged("shoes"))
        testDispatcher.scheduler.advanceTimeBy(301)

        viewModel.processIntent(SearchIntent.ClearQuery)
        testDispatcher.scheduler.advanceTimeBy(301)

        val state = viewModel.uiState.value
        assertTrue(state.filteredProducts.isEmpty())
        assertTrue(!state.hasSearched)
    }

    @Test
    fun `no matches sets hasSearched true with empty results`() = runTest(testDispatcher) {
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
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.processIntent(SearchIntent.QueryChanged("shoes"))
        testDispatcher.scheduler.advanceTimeBy(301)

        assertTrue(viewModel.uiState.value.recentSearches.contains("shoes"))
    }

    @Test
    fun `query with no matches is not added to recent searches`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.processIntent(SearchIntent.QueryChanged("nonexistent"))
        testDispatcher.scheduler.advanceTimeBy(301)

        assertTrue(viewModel.uiState.value.recentSearches.isEmpty())
    }

    @Test
    fun `duplicate recent search moves to front instead of duplicating`() =
        runTest(testDispatcher) {
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
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.processIntent(SearchIntent.QueryChanged("shoes"))
        testDispatcher.scheduler.advanceTimeBy(301)

        viewModel.processIntent(SearchIntent.ClearRecentSearches)

        assertTrue(viewModel.uiState.value.recentSearches.isEmpty())
    }

    @Test
    fun `recent search click re-triggers filter for that query`() = runTest(testDispatcher) {
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
        val vm = SearchViewModel(getProductsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state.error != null)
        assertTrue(!state.isLoading)
    }

    @Test
    fun `retry after failure reloads products successfully`() = runTest(testDispatcher) {
        coEvery { getProductsUseCase() } returns flow { throw Exception("network down") }
        val vm = SearchViewModel(getProductsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.uiState.value.error != null)

        coEvery { getProductsUseCase() } returns flowOf(sampleProducts)
        vm.processIntent(SearchIntent.RetryLoad)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertNull(state.error)
        assertEquals(2, state.allProducts.size)
    }

    @Test
    fun `available categories are derived and deduplicated from loaded products`() = runTest(testDispatcher) {
        val productsWithDuplicateCategories = listOf(
            Product(
                id = 1, title = "Silver Earrings", price = 120.0,
                description = "d", category = "Jewelry", imageUrl = "", rating = 4.8f
            ),
            Product(
                id = 2, title = "Gold Ring", price = 200.0,
                description = "d", category = "Jewelry", imageUrl = "", rating = 4.6f
            ),
            Product(
                id = 3, title = "Running Shoes", price = 185.0,
                description = "d", category = "Footwear", imageUrl = "", rating = 4.9f
            )
        )
        coEvery { getProductsUseCase() } returns flowOf(productsWithDuplicateCategories)
        val vm = SearchViewModel(getProductsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val categories = vm.uiState.value.availableCategories
        assertEquals(2, categories.size)
        assertTrue(categories.contains("Jewelry"))
        assertTrue(categories.contains("Footwear"))
    }

    @Test
    fun `category chip click filters results same as recent search click`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        // Reusing RecentSearchClicked intent — category chips and recent chips share behavior
        viewModel.processIntent(SearchIntent.RecentSearchClicked("Jewelry"))
        testDispatcher.scheduler.advanceTimeBy(301)

        val state = viewModel.uiState.value
        assertEquals("Jewelry", state.query)
        assertEquals(1, state.filteredProducts.size)
        assertEquals("Silver Earrings", state.filteredProducts.first().title)
    }
}
