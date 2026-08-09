package com.example.catalogapp.data.product.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.catalogapp.domain.product.ProductRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ProductSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val productRepository: ProductRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result =
        productRepository.syncProducts().toWorkerResult(runAttemptCount)

    companion object {
        const val WORK_NAME = "ProductSyncWorker"
    }
}



