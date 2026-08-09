package com.example.catalogapp.data.product.sync

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.catalogapp.data.product.worker.ProductSyncWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<ProductSyncWorker>(
            repeatInterval = SYNC_INTERVAL_HOURS,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF_DELAY_MINUTES, TimeUnit.MINUTES)
            .addTag(ProductSyncWorker.WORK_NAME)
            .build()

        workManager.enqueueUniquePeriodicWork(
            ProductSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    fun cancelSync() {
        workManager.cancelUniqueWork(ProductSyncWorker.WORK_NAME)
    }

    companion object {
        private const val SYNC_INTERVAL_HOURS = 6L
        private const val BACKOFF_DELAY_MINUTES = 15L
    }
}
