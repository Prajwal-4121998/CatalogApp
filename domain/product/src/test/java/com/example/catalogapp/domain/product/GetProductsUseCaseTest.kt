package com.example.catalogapp.domain.product

import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GetProductsUseCaseTest {

    private val repository: ProductRepository = mockk()
    private val useCase = GetProductsUseCase(repository)

    @Test
    fun `invoke returns products from repository`() = runBlocking {
        // Arrange — fake data, no real network, no real database
        val fakeProducts = listOf(
            Product(
                id = 1,
                title = "Gold Ring",
                price = 4500.0,
                description = "22K gold ring",
                category = "rings",
                imageUrl = "https://example.com/ring.png",
                rating = 4.5f
            )
        )
        every { repository.getProducts() } returns flowOf(fakeProducts)

        // Act
        val result = useCase().first()

        // Assert
        assertEquals(fakeProducts, result)
    }
}
