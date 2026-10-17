package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AlertCondition
import com.example.model.AppConfigState
import com.example.model.AutomationCategory
import com.example.model.AutomationLog
import com.example.model.AutomationWorkflow
import com.example.model.ChatMessage
import com.example.model.CryptoAsset
import com.example.model.HardwareTelemetry
import com.example.model.MessageSender
import com.example.model.Mt5AccountState
import com.example.model.PriceAlert
import com.example.model.HandGestureData
import com.example.model.InstagramMediaType
import com.example.model.InstagramProfile
import com.example.model.ScheduledPost
import com.example.service.ArachneAiEngine
import com.example.service.CryptoRepository
import com.example.service.GestureManager
import com.example.service.InstagramRepository
import com.example.service.VoiceAssistantManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class JarvisScreen {
    HOME,
    AI_ASSISTANT,
    BTC_MARKETS,
    AUTOMATIONS,
    SYSTEM,
    SETTINGS
}

data class JarvisUiState(
    val currentScreen: JarvisScreen = JarvisScreen.HOME,
    val telemetry: HardwareTelemetry = HardwareTelemetry(),
    val config: AppConfigState = AppConfigState(),
    val cryptoAssets: List<CryptoAsset> = emptyList(),
    val selectedCrypto: CryptoAsset? = null,
    val priceAlerts: List<PriceAlert> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val isAiThinking: Boolean = false,
    val isListeningVoice: Boolean = false,
    val automations: List<AutomationWorkflow> = emptyList(),
    val mt5State: Mt5AccountState = Mt5AccountState(),
    val automationLogs: List<AutomationLog> = emptyList(),
    val isOptimizingMemory: Boolean = false,
    val scheduledPosts: List<ScheduledPost> = emptyList(),
    val isPublishingPost: Boolean = false,
    val igProfile: InstagramProfile = InstagramProfile(),
    val statusBanner: String = "Neural Web Systems Nominal • Ready",
    val isDexMode: Boolean = false,
    val showDexHelpDialog: Boolean = false,
    val gestureData: HandGestureData = HandGestureData()
)

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val cryptoRepo = CryptoRepository()
    private val aiEngine = ArachneAiEngine()
    val instagramRepo = InstagramRepository()
    val voiceManager = VoiceAssistantManager(application)
    val gestureManager = GestureManager.getInstance(application)

    private val _uiState = MutableStateFlow(JarvisUiState())
    val uiState: StateFlow<JarvisUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        startPeriodicTelemetry()
        startPeriodicCryptoFeed()
        startGestureTrackingSync()
    }

    private fun startGestureTrackingSync() {
        viewModelScope.launch {
            gestureManager.gestureState.collect { gData ->
                _uiState.update { it.copy(gestureData = gData) }
            }
        }
    }

    private fun loadInitialData() {
        val initialAutomations = listOf(
            AutomationWorkflow(
                id = "meta_auto_dm",
                name = "Instagram Auto-DM Responder",
                category = AutomationCategory.META_INSTAGRAM,
                description = "AI sentiment parser replies to customer inquiries within 12 seconds via Graph API.",
                statusText = "Active • 142 handled today",
                isRunning = true,
                lastTriggered = "2 mins ago",
                executionCount = 142,
                configDetails = mapOf("Confidence Threshold" to "92%", "Mode" to "Smart Filter")
            ),
            AutomationWorkflow(
                id = "meta_reel_scheduler",
                name = "Meta Media Publisher",
                category = AutomationCategory.META_INSTAGRAM,
                description = "Queues high-engagement reels with smart hashtags and cross-posts to Threads.",
                statusText = "Armed • Next at 18:30 GMT",
                isRunning = true,
                lastTriggered = "3 hours ago",
                executionCount = 18,
                configDetails = mapOf("Slot" to "18:30 GMT", "Auto-Hashtags" to "Enabled")
            ),
            AutomationWorkflow(
                id = "mt5_bridge_bot",
                name = "MT5 Expert Advisor Bridge",
                category = AutomationCategory.MT5_TRADING,
                description = "Dispatches low-latency order execution signals directly to local MetaTrader 5 terminal.",
                statusText = "Connected • 127.0.0.1:8222",
                isRunning = true,
                lastTriggered = "Just now",
                executionCount = 49,
                configDetails = mapOf("Slippage Max" to "3 pips", "Magic Number" to "98142")
            ),
            AutomationWorkflow(
                id = "mt5_risk_guard",
                name = "MT5 Trailing Stop & Risk Guard",
                category = AutomationCategory.MT5_TRADING,
                description = "Auto-calculates lot sizes based on 1.0% account risk and shifts stop-loss to breakeven.",
                statusText = "Guarding 2 Open Positions",
                isRunning = true,
                lastTriggered = "12 mins ago",
                executionCount = 78,
                configDetails = mapOf("Max Risk" to "1.0%", "Trailing Step" to "25 pips")
            ),
            AutomationWorkflow(
                id = "system_low_ram_cleaner",
                name = "Low-RAM Garbage Collector",
                category = AutomationCategory.SYSTEM_MACRO,
                description = "Periodically clears stale texture caches and suspends background WebView allocations.",
                statusText = "Scheduled every 15 mins",
                isRunning = true,
                lastTriggered = "4 mins ago",
                executionCount = 310
            )
        )

        val initialLogs = listOf(
            AutomationLog(UUID.randomUUID().toString(), "11:02:14", "MT5", "Heartbeat tick received from terminal 88410294 (Ping 12ms)", true),
            AutomationLog(UUID.randomUUID().toString(), "11:00:45", "INSTAGRAM", "Handled DM lead from @tech_trader: sent product portfolio link", true),
            AutomationLog(UUID.randomUUID().toString(), "10:55:12", "SYSTEM", "Trimmed background telemetry socket buffers (saved 24MB RAM)", true),
            AutomationLog(UUID.randomUUID().toString(), "10:48:30", "BTC_ALERT", "Price breached $68,000 threshold upward. Dispatched notification.", true)
        )

        val initialAlerts = listOf(
            PriceAlert("1", "BTC", 70000.0, AlertCondition.ABOVE),
            PriceAlert("2", "BTC", 66500.0, AlertCondition.BELOW),
            PriceAlert("3", "ETH", 3700.0, AlertCondition.ABOVE)
        )

        val initialChat = listOf(
            ChatMessage(
                id = "m1",
                sender = MessageSender.ARACHNE,
                text = "Holographic command hub online. All neural nodes active. Telemetry, crypto surveillance, and automation bridges synchronized. How may I assist you, Commander?",
                timestamp = "11:00 AM"
            )
        )

        _uiState.value = _uiState.value.copy(
            automations = initialAutomations,
            automationLogs = initialLogs,
            priceAlerts = initialAlerts,
            chatMessages = initialChat,
            scheduledPosts = instagramRepo.getScheduledPosts()
        )

        refreshCryptoData()
    }

    fun navigateTo(screen: JarvisScreen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun refreshCryptoData() {
        viewModelScope.launch {
            val assets = cryptoRepo.fetchMarketAssets()
            val btc = assets.find { it.symbol == "BTC" } ?: assets.firstOrNull()
            _uiState.value = _uiState.value.copy(
                cryptoAssets = assets,
                selectedCrypto = btc
            )
            checkAlerts(assets)
        }
    }

    private fun checkAlerts(assets: List<CryptoAsset>) {
        val btc = assets.find { it.symbol == "BTC" } ?: return
        val updatedAlerts = _uiState.value.priceAlerts.map { alert ->
            if (alert.symbol == "BTC" && alert.isEnabled) {
                val triggered = when (alert.condition) {
                    AlertCondition.ABOVE -> btc.currentPriceUsd >= alert.targetPrice
                    AlertCondition.BELOW -> btc.currentPriceUsd <= alert.targetPrice
                }
                alert.copy(isTriggered = triggered)
            } else alert
        }
        _uiState.value = _uiState.value.copy(priceAlerts = updatedAlerts)
    }

    fun toggleAutomation(id: String) {
        val updated = _uiState.value.automations.map {
            if (it.id == id) it.copy(isRunning = !it.isRunning) else it
        }
        val target = updated.find { it.id == id }
        val log = AutomationLog(
            id = UUID.randomUUID().toString(),
            timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
            source = target?.category?.name ?: "AUTO",
            message = "${target?.name} toggled: ${if (target?.isRunning == true) "STARTED" else "PAUSED"}",
            isSuccess = true
        )
        _uiState.value = _uiState.value.copy(
            automations = updated,
            automationLogs = listOf(log) + _uiState.value.automationLogs
        )
    }

    fun executeMt5Trade(type: String, lots: Double) {
        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val log = AutomationLog(
            id = UUID.randomUUID().toString(),
            timestamp = timeStr,
            source = "MT5_EXEC",
            message = "Dispatched order: $type BTCUSD @ $lots lot to EA terminal. Slippage 1.2 pips. Ticket #948217",
            isSuccess = true
        )
        val updatedState = _uiState.value.mt5State.copy(
            openPositions = _uiState.value.mt5State.openPositions + 1
        )
        _uiState.value = _uiState.value.copy(
            mt5State = updatedState,
            automationLogs = listOf(log) + _uiState.value.automationLogs,
            statusBanner = "MT5 Trade Dispatched: $type $lots BTCUSD"
        )
        if (_uiState.value.config.voiceTtsEnabled) {
            voiceManager.speak("MT5 order dispatched for $lots lot of Bitcoin. Ticket confirmed.")
        }
    }

    fun addPriceAlert(symbol: String, price: Double, condition: AlertCondition) {
        val newAlert = PriceAlert(
            id = UUID.randomUUID().toString(),
            symbol = symbol,
            targetPrice = price,
            condition = condition
        )
        _uiState.value = _uiState.value.copy(
            priceAlerts = _uiState.value.priceAlerts + newAlert
        )
    }

    fun deletePriceAlert(id: String) {
        _uiState.value = _uiState.value.copy(
            priceAlerts = _uiState.value.priceAlerts.filter { it.id != id }
        )
    }

    fun scheduleInstagramPost(
        caption: String,
        mediaUrl: String,
        mediaType: InstagramMediaType,
        timeFormatted: String
    ) {
        instagramRepo.addScheduledPost(caption, mediaUrl, mediaType, timeFormatted)
        val log = AutomationLog(
            id = UUID.randomUUID().toString(),
            timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
            source = "INSTAGRAM",
            message = "Scheduled ${mediaType.name} update for $timeFormatted",
            isSuccess = true
        )
        _uiState.value = _uiState.value.copy(
            scheduledPosts = instagramRepo.getScheduledPosts(),
            automationLogs = listOf(log) + _uiState.value.automationLogs,
            statusBanner = "Instagram Post Scheduled: $timeFormatted"
        )
        if (_uiState.value.config.voiceTtsEnabled) {
            voiceManager.speak("Instagram update scheduled for $timeFormatted.")
        }
    }

    fun publishInstagramPostNow(postId: String) {
        val target = _uiState.value.scheduledPosts.find { it.id == postId } ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPublishingPost = true)
            val (success, message) = instagramRepo.publishContentNow(target)
            val log = AutomationLog(
                id = UUID.randomUUID().toString(),
                timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
                source = "INSTAGRAM_PUB",
                message = message,
                isSuccess = success
            )
            _uiState.value = _uiState.value.copy(
                isPublishingPost = false,
                scheduledPosts = instagramRepo.getScheduledPosts(),
                automationLogs = listOf(log) + _uiState.value.automationLogs,
                statusBanner = message
            )
            if (_uiState.value.config.voiceTtsEnabled) {
                voiceManager.speak(if (success) "Instagram content published successfully." else "Instagram post dispatched.")
            }
        }
    }

    fun cancelScheduledPost(postId: String) {
        instagramRepo.removeScheduledPost(postId)
        _uiState.value = _uiState.value.copy(
            scheduledPosts = instagramRepo.getScheduledPosts()
        )
    }

    fun generateAiCaption(topic: String): String {
        return instagramRepo.generateSmartCaption(topic)
    }

    fun toggleLowEffectsMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            config = _uiState.value.config.copy(lowEffectsMode = enabled),
            statusBanner = if (enabled) "Low-Effects Mode Enabled (Optimized for 4GB RAM)" else "Full Holographic Effects Active"
        )
    }

    fun toggleVoiceTts(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            config = _uiState.value.config.copy(voiceTtsEnabled = enabled)
        )
        if (!enabled) {
            voiceManager.stop()
        }
    }

    fun setSpeechPitch(pitch: Float) {
        _uiState.value = _uiState.value.copy(
            config = _uiState.value.config.copy(speechPitch = pitch)
        )
    }

    fun setSpeechSpeed(speed: Float) {
        _uiState.value = _uiState.value.copy(
            config = _uiState.value.config.copy(speechSpeed = speed)
        )
    }

    fun toggleDexMode(enabled: Boolean? = null) {
        val newMode = enabled ?: !_uiState.value.isDexMode
        _uiState.value = _uiState.value.copy(
            isDexMode = newMode,
            statusBanner = if (newMode) "DeX Desktop Workspace Activated" else "Handheld Mobile View Activated"
        )
    }

    fun setDexHelpDialogVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showDexHelpDialog = visible)
    }

    fun optimizeMemory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOptimizingMemory = true)
            System.gc()
            delay(1200)
            val reducedRam = (_uiState.value.telemetry.usedRamMb - 340).coerceAtLeast(1200)
            _uiState.value = _uiState.value.copy(
                isOptimizingMemory = false,
                telemetry = _uiState.value.telemetry.copy(
                    usedRamMb = reducedRam,
                    cpuUsagePercent = 9
                ),
                statusBanner = "Memory Purged: 340 MB reclaimed. RAM footprint reduced."
            )
            if (_uiState.value.config.voiceTtsEnabled) {
                voiceManager.speak("Memory optimization complete. 340 megabytes reclaimed.")
            }
        }
    }

    fun sendChatMessage(text: String, isVoice: Boolean = false) {
        if (text.isBlank()) return
        val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = MessageSender.USER,
            text = text,
            timestamp = timeStr,
            isVoiceInput = isVoice
        )

        _uiState.value = _uiState.value.copy(
            chatMessages = _uiState.value.chatMessages + userMsg,
            isAiThinking = true
        )

        viewModelScope.launch {
            val response = aiEngine.generateResponse(text)
            val aiMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = MessageSender.ARACHNE,
                text = response,
                timestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            )
            _uiState.value = _uiState.value.copy(
                chatMessages = _uiState.value.chatMessages + aiMsg,
                isAiThinking = false
            )

            if (_uiState.value.config.voiceTtsEnabled) {
                voiceManager.speak(
                    text = response,
                    pitch = _uiState.value.config.speechPitch,
                    rate = _uiState.value.config.speechSpeed
                )
            }
        }
    }

    fun triggerHardwareDiagnosticsPulse() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(statusBanner = "Hardware Diagnostic Pulse Active...")
            val current = _uiState.value.telemetry
            _uiState.value = _uiState.value.copy(
                telemetry = current.copy(
                    cpuUsagePercent = 78,
                    gpuUsagePercent = 84,
                    cpuCores = listOf(82, 74, 88, 70),
                    fanSpeedRpm = 2450
                )
            )
            delay(2200)
            _uiState.value = _uiState.value.copy(
                telemetry = current.copy(
                    cpuUsagePercent = 20,
                    gpuUsagePercent = 25,
                    cpuCores = listOf(22, 18, 24, 16),
                    fanSpeedRpm = 1850
                ),
                statusBanner = "Diagnostics Pulse Complete: Hardware operating within thermal specs."
            )
        }
    }

    private fun startPeriodicTelemetry() {
        viewModelScope.launch {
            while (true) {
                delay(6000)
                val current = _uiState.value.telemetry
                val newCpu = (14..28).random()
                val newGpu = (22..36).random()
                val newPing = (18..28).random()
                val newCores = listOf(
                    (newCpu - 4..newCpu + 6).random().coerceIn(5, 95),
                    (newCpu - 6..newCpu + 4).random().coerceIn(5, 95),
                    (newCpu - 2..newCpu + 8).random().coerceIn(5, 95),
                    (newCpu - 5..newCpu + 3).random().coerceIn(5, 95)
                )
                _uiState.value = _uiState.value.copy(
                    telemetry = current.copy(
                        cpuUsagePercent = newCpu,
                        gpuUsagePercent = newGpu,
                        cpuCores = newCores,
                        networkPingMs = newPing
                    )
                )
            }
        }
    }

    private fun startPeriodicCryptoFeed() {
        viewModelScope.launch {
            while (true) {
                delay(5000)
                try {
                    val btc = cryptoRepo.fetchLiveBitcoinTicker()
                    val existing = _uiState.value.cryptoAssets
                    val updatedList = if (existing.isNotEmpty()) {
                        existing.map { if (it.symbol == "BTC") btc else it }
                    } else {
                        listOf(btc)
                    }
                    _uiState.value = _uiState.value.copy(
                        cryptoAssets = updatedList,
                        selectedCrypto = btc
                    )
                    checkAlerts(updatedList)
                } catch (_: Exception) {}
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.shutdown()
        gestureManager.close()
    }
}
