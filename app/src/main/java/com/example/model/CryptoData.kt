package com.example.model

data class CryptoAsset(
    val id: String,
    val symbol: String,
    val name: String,
    val currentPriceUsd: Double,
    val change24hPercent: Double,
    val high24h: Double,
    val low24h: Double,
    val marketCapUsd: String,
    val volume24hUsd: String,
    val rsi14: Double,
    val macdSignal: String,
    val priceHistory: List<Float>
)

data class PriceAlert(
    val id: String,
    val symbol: String,
    val targetPrice: Double,
    val condition: AlertCondition,
    val isTriggered: Boolean = false,
    val isEnabled: Boolean = true
)

enum class AlertCondition {
    ABOVE,
    BELOW
}
