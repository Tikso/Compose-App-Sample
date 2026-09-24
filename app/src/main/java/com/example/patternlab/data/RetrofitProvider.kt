package com.example.patternlab.data

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitProvider {
    private const val BASE_URL = "https://rickandmortyapi.com/api/" // must end with "/"

    // Ignore JSON keys we didn't declare in our DTOs instead of crashing.
    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        // BASIC = only request line + status + timing. BODY would dump the huge episode lists.
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    val api: RickAndMortyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF-8".toMediaType()))
            .build()
            .create(RickAndMortyApi::class.java)
    }
}