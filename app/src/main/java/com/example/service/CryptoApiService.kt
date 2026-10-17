package com.example.service

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = false)
data class BinanceTickerResponse(
    @field:Json(name = "symbol") val symbol: String = "BTCUSDT",
    @field:Json(name = "lastPrice") val lastPrice: String = "68450.00",
    @field:Json(name = "priceChangePercent") val priceChangePercent: String = "3.42",
    @field:Json(name = "highPrice") val highPrice: String = "69120.00",
    @field:Json(name = "lowPrice") val lowPrice: String = "66890.00",
    @field:Json(name = "volume") val volume: String = "28450.50",
    @field:Json(name = "quoteVolume") val quoteVolume: String = "1945200000.00"
)

interface BinanceApiService {
    @GET("api/v3/ticker/24hr")
    suspend fun get24hTicker(
        @Query("symbol") symbol: String = "BTCUSDT"
    ): Response<BinanceTickerResponse>
}

object RetrofitClientProvider {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .build()

    val binanceService: BinanceApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.binance.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(BinanceApiService::class.java)
    }
}
