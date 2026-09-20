package com.example.catalogapp.data.product.repository

import com.example.catalogapp.core.network.ConnectivityChecker
import com.example.catalogapp.core.network.api.ProductApiService
import com.example.catalogapp.core.network.dto.ProductDto
import com.example.catalogapp.core.network.dto.RatingDto
import com.example.catalogapp.data.product.local.dao.ProductDao
import com.example.catalogapp.data.product.local.entity.ProductEntity
import com.example.catalogapp.domain.product.SyncError
import com.example.catalogapp.domain.product.SyncResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class ProductRepositoryImplTest {

    private val apiService: ProductApiService = mockk()
    private val productDao: ProductDao = mockk()
    private val connectivityChecker: ConnectivityChecker = mockk()
    private lateinit var repository: ProductRepositoryImpl

    private val fakeEntity = ProductEntity(
        id = 1,
        title = "Gold Ring",
        price = 4500.0,
        description = "22K gold ring",
        category = "rings",
        imageUrl = "https://example.com/ring.png",
        rating = 4.5f,
        ratingCount = 100,
        cachedAt = System.currentTimeMillis()
    )

    private val fakeDto = ProductDto(
        id = 2,
        title = "Silver Necklace",
        price = 2500.0,
        description = "Pure silver necklace",
        category = "necklaces",
        imageUrl = "https://example.com/necklace.png",
        rating = RatingDto(rate = 4.2f, count = 50)
    )

    @Before
    fun setUp() {
        repository = ProductRepositoryImpl(apiService, productDao, connectivityChecker)
    }

    @Test
    fun `getProducts returns mapped domain list from Room`() = runTest {
        every { productDao.getAllProducts() } returns flowOf(listOf(fakeEntity))

        val result = repository.getProducts().first()

        assertEquals(1, result.size)
        assertEquals("Gold Ring", result[0].title)
        assertEquals(4500.0, result[0].price, 0.0)
    }


    @Test
    fun `getProductById returns cached entity when available`() = runTest {
        coEvery { productDao.getProductById(1) } returns fakeEntity

        val result = repository.getProductById(1)

        assertEquals("Gold Ring", result?.title)
        coVerify(exactly = 0) { apiService.getProductById(any()) }
    }

    @Test
    fun `getProductById fetches from network when not in cache and connected`() = runTest {
        coEvery { productDao.getProductById(2) } returns null
        every { connectivityChecker.isConnected() } returns true
        coEvery { apiService.getProductById(2) } returns fakeDto

        val result = repository.getProductById(2)

        assertEquals("Silver Necklace", result?.title)
        coVerify { apiService.getProductById(2) }
    }

    @Test
    fun `getProductById returns null when not in cache and offline`() = runTest {
        coEvery { productDao.getProductById(99) } returns null
        every { connectivityChecker.isConnected() } returns false

        val result = repository.getProductById(99)

        assertNull(result)
        coVerify(exactly = 0) { apiService.getProductById(any()) }
    }

    @Test
    fun `refreshProducts handles SocketTimeoutException and returns Error without touching cache`() =
        runTest {
            every { connectivityChecker.isConnected() } returns true
            coEvery { apiService.getProducts() } throws SocketTimeoutException()

            val result = repository.syncProducts()

            assertTrue(result is SyncResult.Error)
            assertTrue((result as SyncResult.Error).reason is SyncError.Timeout)
            // Critical with the transaction fix: on failure, clearAndInsertProducts
            // must NEVER be called — old cache must survive untouched
            coVerify(exactly = 0) { productDao.clearAndInsertProducts(any()) }
        }

    @Test
    fun `refreshProducts handles HttpException and returns Error without touching cache`() =
        runTest {
            every { connectivityChecker.isConnected() } returns true
            coEvery { apiService.getProducts() } throws HttpException(
                Response.error<Any>(500, "Internal Server Error".toResponseBody(null))
            )

            val result = repository.syncProducts()

            assertTrue(result is SyncResult.Error)
            coVerify(exactly = 0) { productDao.clearAndInsertProducts(any()) }
        }

    @Test
    fun `mapper handles null rating defensively and defaults to zero`() = runTest {
        val dtoWithNullRating = ProductDto(
            id = 3,
            title = "Test Product",
            price = 100.0,
            description = "Test description",
            category = "test",
            imageUrl = "https://example.com/test.png",
            rating = null
        )
        coEvery { productDao.getProductById(3) } returns null
        every { connectivityChecker.isConnected() } returns true
        coEvery { apiService.getProductById(3) } returns dtoWithNullRating

        val result = repository.getProductById(3)

        assertEquals(0f, result?.rating)
        assertEquals("Test Product", result?.title)
    }

    @Test
    fun `syncProducts returns Success and writes via atomic transaction`() = runTest {
        every { connectivityChecker.isConnected() } returns true
        coEvery { apiService.getProducts() } returns listOf(fakeDto)
        coEvery { productDao.clearAndInsertProducts(any()) } returns Unit

        val result = repository.syncProducts()

        assertTrue(result is SyncResult.Success)
        coVerify { apiService.getProducts() }
        coVerify { productDao.clearAndInsertProducts(any()) }
    }

    @Test
    fun `syncProducts returns Error NoInternet when disconnected`() = runTest {
        every { connectivityChecker.isConnected() } returns false

        val result = repository.syncProducts()

        assertTrue(result is SyncResult.Error)
        assertTrue((result as SyncResult.Error).reason is SyncError.NoInternet)
        coVerify(exactly = 0) { apiService.getProducts() }
    }

    @Test
    fun `refreshIfStale returns null and never calls network when cache is fresh`() = runTest {
        val freshTimestamp = System.currentTimeMillis()
        coEvery { productDao.getOldestCacheTimestamp() } returns freshTimestamp

        val result = repository.refreshIfStale()

        assertNull(result)
        coVerify(exactly = 0) { apiService.getProducts() }
    }

    @Test
    fun `refreshIfStale calls network and returns SyncResult when cache is stale`() = runTest {
        val staleTimestamp = System.currentTimeMillis() - (31 * 60 * 1000L)
        coEvery { productDao.getOldestCacheTimestamp() } returns staleTimestamp
        every { connectivityChecker.isConnected() } returns true
        coEvery { apiService.getProducts() } returns listOf(fakeDto)
        coEvery { productDao.clearAndInsertProducts(any()) } returns Unit

        val result = repository.refreshIfStale()

        assertTrue(result is SyncResult.Success)
        coVerify { apiService.getProducts() }
    }

    @Test
    fun `refreshIfStale treats missing cache timestamp as stale on first-ever launch`() = runTest {
        // No timestamp exists yet — shouldRefreshCache() returns true
        coEvery { productDao.getOldestCacheTimestamp() } returns null
        every { connectivityChecker.isConnected() } returns true
        coEvery { apiService.getProducts() } returns listOf(fakeDto)
        coEvery { productDao.clearAndInsertProducts(any()) } returns Unit

        val result = repository.refreshIfStale()

        assertTrue(result is SyncResult.Success)
        coVerify { apiService.getProducts() }
    }
}