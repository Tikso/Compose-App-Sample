package com.example.patternlab.domain.model

/**
 * Failures described in domain terms. The data layer translates HttpException / IOException
 * into these, so nothing above it needs to know that Retrofit or OkHttp exist.
 */
sealed class DataError : Exception() {
    data object NoInternet : DataError()
    data class Server(val code: Int) : DataError()
    data class Unknown(override val cause: Throwable) : DataError()
}
