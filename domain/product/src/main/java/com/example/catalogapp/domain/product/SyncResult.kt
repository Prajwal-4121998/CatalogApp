package com.example.catalogapp.domain.product

sealed class SyncResult {
    object Success : SyncResult()
    data class Error(val reason: SyncError) : SyncResult()
}

sealed class SyncError {
    object Timeout : SyncError()
    object NoInternet : SyncError()
    data class ServerError(val code: Int, val message: String?) : SyncError()
}
