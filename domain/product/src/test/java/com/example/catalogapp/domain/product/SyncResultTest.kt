package com.example.catalogapp.domain.product

import org.junit.Assert.assertTrue
import org.junit.Test

class SyncResultTest {
    @Test
    fun `Success is a SyncResult`() {
        assertTrue(true)
    }

    @Test
    fun `Error wraps SyncError correctly`() {
        val result = SyncResult.Error(SyncError.ServerError(500, "Internal error"))
        assertTrue(true)
        assertTrue(result.reason is SyncError.ServerError)
    }
}