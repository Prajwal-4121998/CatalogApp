package com.example.catalogapp.domain.product

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class SearchProductsUseCaseTest {

    private val useCase = SearchProductsUseCase()

    private val products = listOf(
        Product(
            id = 1, title = "Silver Earrings", price = 120.0,
            description = "d", category = "Jewelry", imageUrl = "", rating = 4.8f
        ),
        Product(
            id = 2, title = "Running Shoes", price = 185.0,
            description = "d", category = "Footwear", imageUrl = "", rating = 4.9f
        ),
        Product(
            id = 3, title = "Gold Necklace", price = 300.0,
            description = "d", category = "Jewelry", imageUrl = "", rating = 4.7f
        )
    )

    @Test
    fun `blank query returns empty list`() {
        val result = useCase(products, "")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `matches by title substring`() {
        val result = useCase(products, "Earrings")
        assertEquals(1, result.size)
        assertEquals("Silver Earrings", result.first().title)
    }

    @Test
    fun `matches by category substring`() {
        val result = useCase(products, "Jewelry")
        assertEquals(2, result.size)
        assertTrue(result.all { it.category == "Jewelry" })
    }

    @Test
    fun `match is case-insensitive`() {
        val result = useCase(products, "SILVER")
        assertEquals(1, result.size)
    }

    @Test
    fun `no matches returns empty list`() {
        val result = useCase(products, "nonexistent")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `empty product list returns empty list regardless of query`() {
        val result = useCase(emptyList(), "silver")
        assertTrue(result.isEmpty())
    }
}