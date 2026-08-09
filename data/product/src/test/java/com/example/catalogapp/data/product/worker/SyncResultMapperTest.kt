package com.example.catalogapp.data.product.worker

import androidx.work.ListenableWorker.Result
import com.example.catalogapp.domain.product.SyncError
import com.example.catalogapp.domain.product.SyncResult
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncResultMapperTest {

    @Test
    fun `Success maps to Result Success`() {
        val result = SyncResult.Success.toWorkerResult(runAttemptCount = 0)
        assertTrue(result is Result.Success)
    }

    @Test
    fun `Timeout retries under max attempts`() {
        val result = SyncResult.Error(SyncError.Timeout).toWorkerResult(runAttemptCount = 1)
        assertTrue(result is Result.Retry)
    }

    @Test
    fun `Timeout fails after max attempts exhausted`() {
        val result = SyncResult.Error(SyncError.Timeout).toWorkerResult(runAttemptCount = 3)
        assertTrue(result is Result.Failure)
    }

    @Test
    fun `NoInternet retries under max attempts`() {
        val result = SyncResult.Error(SyncError.NoInternet).toWorkerResult(runAttemptCount = 0)
        assertTrue(result is Result.Retry)
    }

    @Test
    fun `5xx server error retries`() {
        val result = SyncResult.Error(SyncError.ServerError(503, "Unavailable"))
            .toWorkerResult(runAttemptCount = 0)
        assertTrue(result is Result.Retry)
    }

    @Test
    fun `4xx server error fails fast without retry`() {
        val result = SyncResult.Error(SyncError.ServerError(400, "Bad Request"))
            .toWorkerResult(runAttemptCount = 0)
        assertTrue(result is Result.Failure)
    }
}