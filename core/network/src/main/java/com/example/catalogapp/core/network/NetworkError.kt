package com.example.catalogapp.core.network

sealed class NetworkError : Exception() {
    object NoInternet : NetworkError()
    object Timeout : NetworkError()
    data class ServerError(val code: Int, val errorMessage: String?) : NetworkError()
    data class Unknown(val errorMessage: String?) : NetworkError()
}
