package com.example.model

enum class AutomationCategory {
    META_INSTAGRAM,
    MT5_TRADING,
    SYSTEM_MACRO
}

data class AutomationWorkflow(
    val id: String,
    val name: String,
    val category: AutomationCategory,
    val description: String,
    val statusText: String,
    val isRunning: Boolean,
    val lastTriggered: String,
    val executionCount: Int,
    val configDetails: Map<String, String> = emptyMap()
)

data class Mt5AccountState(
    val broker: String = "MetaQuotes-Demo / Live Bridge",
    val accountId: String = "88410294",
    val serverStatus: String = "127.0.0.1:8222 (EA Bridge Online)",
    val balance: Double = 10000.0,
    val equity: Double = 10420.50,
    val openPositions: Int = 2,
    val marginLevel: Double = 842.1
)

data class AutomationLog(
    val id: String,
    val timestamp: String,
    val source: String,
    val message: String,
    val isSuccess: Boolean
)
