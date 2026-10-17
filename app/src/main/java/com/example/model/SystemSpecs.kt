package com.example.model

enum class MessageSender {
    USER,
    ARACHNE,
    SYSTEM
}

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: String,
    val isVoiceInput: Boolean = false,
    val actionType: String? = null,
    val actionPayload: String? = null
)

data class HardwareTelemetry(
    val processorName: String = "Intel(R) Core(TM) i5-5200U CPU @ 2.20GHz",
    val cpuUsagePercent: Int = 18,
    val cpuCores: List<Int> = listOf(22, 16, 28, 14),
    val cpuTempC: Int = 42,
    val totalRamMb: Int = 4096,
    val usedRamMb: Int = 1840,
    val ramCachedMb: Int = 780,
    val gpuName: String = "Intel(R) HD Graphics 5500",
    val gpuUsagePercent: Int = 28,
    val gpuVramUsedMb: Int = 640,
    val gpuVramTotalMb: Int = 2048,
    val gpuTempC: Int = 45,
    val gpuClockMhz: Int = 900,
    val fanSpeedRpm: Int = 1850,
    val storageTotalGb: Int = 256,
    val storageUsedGb: Int = 124,
    val batteryPercent: Int = 86,
    val isCharging: Boolean = true,
    val networkPingMs: Int = 24,
    val thermalState: String = "Nominal (42°C)",
    val architecture: String = "x86_64 / Windows 10 Dual Bridge"
)

data class AppConfigState(
    val lowEffectsMode: Boolean = false,
    val voiceTtsEnabled: Boolean = true,
    val speechPitch: Float = 1.05f,
    val speechSpeed: Float = 1.0f,
    val crimsonAccentPercent: Int = 50,
    val cyanAccentPercent: Int = 85,
    val metaApiToken: String = "EAAG...meta_oauth_verified",
    val mt5BridgePort: Int = 8222
)
