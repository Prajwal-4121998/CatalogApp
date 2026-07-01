package com.example.catalogapp.data.product.repository

import com.example.catalogapp.core.network.ConnectivityChecker
import com.example.catalogapp.core.network.api.ProductApiService
import com.example.catalogapp.core.network.dto.ProductDto
import com.example.catalogapp.core.network.dto.RatingDto
import com.example.catalogapp.data.product.local.dao.ProductDao
import com.example.catalogapp.data.product.local.entity.ProductEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

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
    fun `getProducts returns mapped domain list from Room when cache is fresh`() = runTest {
        // Arrange — cache is fresh (cachedAt is now)
        val freshTimestamp = System.currentTimeMillis()
        every { productDao.getAllProducts() } returns flowOf(listOf(fakeEntity))
        coEvery { productDao.getOldestCacheTimestamp() } returns freshTimestamp

        // Act
        val result = repository.getProducts().first()

        // Assert — entity mapped to domain correctly
        assertEquals(1, result.size)
        assertEquals("Gold Ring", result[0].title)
        assertEquals(4500.0, result[0].price, 0.0)
    }

    @Test
    fun `getProducts triggers network refresh when cache is stale`() = runTest {
        // Arrange — cache is stale (older than 30 minutes)
        val staleTimestamp = System.currentTimeMillis() - (31 * 60 * 1000L)
        every { productDao.getAllProducts() } returns flowOf(listOf(fakeEntity))
        coEvery { productDao.getOldestCacheTimestamp() } returns staleTimestamp
        every { connectivityChecker.isConnected() } returns true
        coEvery { apiService.getProducts() } returns listOf(fakeDto)
        coEvery { productDao.clearAll() } returns Unit
        coEvery { productDao.insertProducts(any()) } returns Unit

        // Act
        repository.getProducts().first()

        // Assert — network was called and DAO was updated
        coVerify { apiService.getProducts() }
        coVerify { productDao.clearAll() }
        coVerify { productDao.insertProducts(any()) }
    }

    @Test
    fun `getProducts does not call network when offline even if cache is stale`() = runTest {
        // Arrange — stale cache + no internet
        val staleTimestamp = System.currentTimeMillis() - (31 * 60 * 1000L)
        every { productDao.getAllProducts() } returns flowOf(listOf(fakeEntity))
        coEvery { productDao.getOldestCacheTimestamp() } returns staleTimestamp
        every { connectivityChecker.isConnected() } returns false

        // Act
        repository.getProducts().first()

        // Assert — network was never called despite stale cache
        coVerify(exactly = 0) { apiService.getProducts() }
    }

    @Test
    fun `getProductById returns cached entity when available`() = runTest {
        // Arrange
        coEvery { productDao.getProductById(1) } returns fakeEntity

        // Act
        val result = repository.getProductById(1)

        // Assert
        assertEquals("Gold Ring", result?.title)
        // Network should never have been called
        coVerify(exactly = 0) { apiService.getProductById(any()) }
    }

    @Test
    fun `getProductById fetches from network when not in cache and connected`() = runTest {
        // Arrange — not in cache, connected
        coEvery { productDao.getProductById(2) } returns null
        every { connectivityChecker.isConnected() } returns true
        coEvery { apiService.getProductById(2) } returns fakeDto

        // Act
        val result = repository.getProductById(2)

        // Assert
        assertEquals("Silver Necklace", result?.title)
        coVerify { apiService.getProductById(2) }
    }

    @Test
    fun `getProductById returns null when not in cache and offline`() = runTest {
        // Arrange — not in cache, offline
        coEvery { productDao.getProductById(99) } returns null
        every { connectivityChecker.isConnected() } returns false

        // Act
        val result = repository.getProductById(99)

        // Assert
        assertNull(result)
        coVerify(exactly = 0) { apiService.getProductById(any()) }
    }
}
