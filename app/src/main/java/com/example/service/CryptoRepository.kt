package com.example.service

import com.example.model.CryptoAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CryptoRepository {

    private val binanceService = RetrofitClientProvider.binanceService

    // Base historical curve points for BTC trendline
    private var btcLiveHistory = mutableListOf(
        66400f, 66850f, 66320f, 67100f, 66950f, 67400f, 67210f, 67800f,
        67650f, 68100f, 68020f, 68450f, 68300f, 68720f, 68510f, 68900f
    )

    private val ethHistoryBase = listOf(
        3420f, 3450f, 3410f, 3480f, 3460f, 3510f, 3495f, 3530f, 3515f, 3560f
    )

    private val solHistoryBase = listOf(
        174f, 172f, 176f, 175f, 180f, 178f, 184f, 182f, 187f, 189f
    )

    /**
     * Fetches real-time Bitcoin ticker via Retrofit
     */
    suspend fun fetchLiveBitcoinTicker(): CryptoAsset = withContext(Dispatchers.IO) {
        try {
            val response = binanceService.get24hTicker("BTCUSDT")
            if (response.isSuccessful && response.body() != null) {
                val ticker = response.body()!!
                val lastPrice = ticker.lastPrice.toDoubleOrNull() ?: 68450.0
                val changePercent = ticker.priceChangePercent.toDoubleOrNull() ?: 3.42
                val high = ticker.highPrice.toDoubleOrNull() ?: (lastPrice * 1.02)
                val low = ticker.lowPrice.toDoubleOrNull() ?: (lastPrice * 0.98)
                val volumeQuote = ticker.quoteVolume.toDoubleOrNull() ?: 1_945_000_000.0

                // Append live price point to history (keep max 24 ticks for fast rendering)
                if (btcLiveHistory.size >= 24) {
                    btcLiveHistory.removeAt(0)
                }
                btcLiveHistory.add(lastPrice.toFloat())

                return@withContext CryptoAsset(
                    id = "bitcoin",
                    symbol = "BTC",
                    name = "Bitcoin",
                    currentPriceUsd = lastPrice,
                    change24hPercent = changePercent,
                    high24h = high,
                    low24h = low,
                    marketCapUsd = "$1.35 T",
                    volume24hUsd = formatVolume(volumeQuote),
                    rsi14 = 64.2,
                    macdSignal = if (changePercent >= 0) "BULLISH CROSS" else "BEARISH MOMENTUM",
                    priceHistory = btcLiveHistory.toList()
                )
            }
        } catch (_: Exception) {
            // Graceful fallback if network drops or endpoint unreachable
        }
        getDefaultBtcAsset()
    }

    suspend fun fetchMarketAssets(): List<CryptoAsset> = withContext(Dispatchers.IO) {
        val btc = fetchLiveBitcoinTicker()

        val defaultEth = CryptoAsset(
            id = "ethereum",
            symbol = "ETH",
            name = "Ethereum",
            currentPriceUsd = 3560.0,
            change24hPercent = 2.15,
            high24h = 3610.0,
            low24h = 3480.0,
            marketCapUsd = "$428 B",
            volume24hUsd = "$16.4 B",
            rsi14 = 58.2,
            macdSignal = "MODERATE BULLISH",
            priceHistory = ethHistoryBase
        )

        val defaultSol = CryptoAsset(
            id = "solana",
            symbol = "SOL",
            name = "Solana",
            currentPriceUsd = 189.4,
            change24hPercent = 5.82,
            high24h = 192.5,
            low24h = 178.0,
            marketCapUsd = "$88.6 B",
            volume24hUsd = "$5.8 B",
            rsi14 = 71.4,
            macdSignal = "STRONG BUY",
            priceHistory = solHistoryBase
        )

        val defaultXrp = CryptoAsset(
            id = "ripple",
            symbol = "XRP",
            name = "XRP Ledger",
            currentPriceUsd = 0.62,
            change24hPercent = -0.84,
            high24h = 0.635,
            low24h = 0.612,
            marketCapUsd = "$34.1 B",
            volume24hUsd = "$1.1 B",
            rsi14 = 47.9,
            macdSignal = "CONSOLIDATING",
            priceHistory = listOf(0.58f, 0.59f, 0.60f, 0.61f, 0.63f, 0.62f)
        )

        listOf(btc, defaultEth, defaultSol, defaultXrp)
    }

    private fun formatVolume(vol: Double): String {
        return when {
            vol >= 1_000_000_000 -> String.format("$%.2f B", vol / 1_000_000_000)
            vol >= 1_000_000 -> String.format("$%.1f M", vol / 1_000_000)
            else -> String.format("$%,.0f", vol)
        }
    }

    private fun getDefaultBtcAsset(): CryptoAsset = CryptoAsset(
        id = "bitcoin",
        symbol = "BTC",
        name = "Bitcoin",
        currentPriceUsd = 68450.0,
        change24hPercent = 3.42,
        high24h = 69120.0,
        low24h = 66890.0,
        marketCapUsd = "$1.34 T",
        volume24hUsd = "$34.2 B",
        rsi14 = 63.8,
        macdSignal = "BULLISH CROSS",
        priceHistory = btcLiveHistory.toList()
    )
}
