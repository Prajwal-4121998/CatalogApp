package com.example.catalogapp.data.product.worker

import androidx.work.ListenableWorker.Result
import com.example.catalogapp.domain.product.SyncError
import com.example.catalogapp.domain.product.SyncResult

/**
 * Pure mapping from domain SyncResult to WorkManager's Result.
 * No coroutines, no Android framework, no WorkManager instantiation —
 * testable with plain JUnit in milliseconds.
 */
internal fun SyncResult.toWorkerResult(runAttemptCount: Int): Result = when (this) {
    is SyncResult.Success -> Result.success()
    is SyncResult.Error -> when (val error = reason) {
        is SyncError.Timeout,
        is SyncError.NoInternet -> retryOrFail(runAttemptCount)

        is SyncError.ServerError ->
            if (error.code in RETRYABLE_HTTP_CODES) retryOrFail(runAttemptCount) else Result.failure()
    }
}

private fun retryOrFail(runAttemptCount: Int): Result =
    if (runAttemptCount < MAX_RETRY_ATTEMPTS) Result.retry() else Result.failure()

private const val MAX_RETRY_ATTEMPTS = 3
private val RETRYABLE_HTTP_CODES = 500..599

