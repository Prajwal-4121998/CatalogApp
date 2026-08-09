package com.example.catalogapp.data.product.repository

import com.example.catalogapp.core.network.ConnectivityChecker
import com.example.catalogapp.core.network.NetworkError
import com.example.catalogapp.core.network.api.ProductApiService
import com.example.catalogapp.data.product.local.dao.ProductDao
import com.example.catalogapp.data.product.mapper.toDomain
import com.example.catalogapp.data.product.mapper.toDomainList
import com.example.catalogapp.data.product.mapper.toEntityList
import com.example.catalogapp.domain.product.Product
import com.example.catalogapp.domain.product.ProductRepository
import com.example.catalogapp.domain.product.SyncError
import com.example.catalogapp.domain.product.SyncResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import retrofit2.HttpException

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ProductApiService,
    private val productDao: ProductDao,
    private val connectivityChecker: ConnectivityChecker
) : ProductRepository {

    companion object {
        // Cache is considered stale after 30 minutes
        private const val CACHE_THRESHOLD_MS = 30 * 60 * 1000L
    }

    override fun getProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
            .map { entities -> entities.toDomainList() }
            .onStart {
                // On first collection, decide whether to refresh from network
                if (shouldRefreshCache()) {
                    refreshProducts()
                }
            }
            .catch { _ ->
                // Room Flow errors are rare but handle defensively
                emit(emptyList())
            }
    }

    override suspend fun getProductById(id: Int): Product? {
        return productDao.getProductById(id)?.toDomain()
            ?: fetchProductByIdFromNetwork(id)
    }

    override suspend fun syncProducts(): SyncResult = refreshProducts()
    // ─── Private helpers ──────────────────────────────────────────────────────

    private suspend fun shouldRefreshCache(): Boolean {
        val oldestTimestamp = productDao.getOldestCacheTimestamp() ?: return true
        return System.currentTimeMillis() - oldestTimestamp > CACHE_THRESHOLD_MS
    }

    // refreshProducts() becomes the single source of truth for both callers
    private suspend fun refreshProducts(): SyncResult {
        if (!connectivityChecker.isConnected()) {
            return SyncResult.Error(SyncError.NoInternet)
        }
        return try {
            val products = apiService.getProducts()
            productDao.clearAll()
            productDao.insertProducts(products.toEntityList())
            SyncResult.Success
        } catch (e: SocketTimeoutException) {
            logError(NetworkError.Timeout, e)
            SyncResult.Error(SyncError.Timeout)
        } catch (e: IOException) {
            logError(NetworkError.NoInternet, e)
            SyncResult.Error(SyncError.NoInternet)
        } catch (e: HttpException) {
            logError(NetworkError.ServerError(e.code(), e.message()), e)
            SyncResult.Error(SyncError.ServerError(e.code(), e.message()))
        }
    }

    private suspend fun fetchProductByIdFromNetwork(id: Int): Product? {
        if (!connectivityChecker.isConnected()) return null
        return try {
            apiService.getProductById(id).toDomain()
        } catch (e: SocketTimeoutException) {
            logError(NetworkError.Timeout, e)
            null
        } catch (e: IOException) {
            logError(NetworkError.NoInternet, e)
            null
        } catch (e: HttpException) {
            logError(NetworkError.ServerError(e.code(), e.message()), e)
            null
        }
    }

    private fun logError(networkError: NetworkError, cause: Throwable) {
        // In a real production app this would go to Firebase Crashlytics or Timber
        // For now: structured logging so the exception is not silently swallowed
        println("NetworkError: ${networkError::class.simpleName} caused by: ${cause.message}")
    }
}
