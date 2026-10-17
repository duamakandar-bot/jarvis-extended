package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlertCondition
import com.example.model.CryptoAsset
import com.example.ui.components.CryptoChart
import com.example.ui.components.HolographicGlassCard
import com.example.ui.components.NeonCyanTrendLineCard
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonWeb
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceBright
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.viewmodel.JarvisUiState

@Composable
fun BtcMarketsScreen(
    uiState: JarvisUiState,
    onRefresh: () -> Unit,
    onAddAlert: (String, Double, AlertCondition) -> Unit,
    onDeleteAlert: (String) -> Unit,
    onQuickTrade: (String, Double) -> Unit
) {
    val btc = uiState.cryptoAssets.find { it.symbol == "BTC" } ?: uiState.cryptoAssets.firstOrNull()
    var selectedTimeframe by remember { mutableStateOf("24H") }
    var showAddAlertSheet by remember { mutableStateOf(false) }
    var targetPriceInput by remember { mutableStateOf("") }
    var alertCondition by remember { mutableStateOf(AlertCondition.ABOVE) }

    val timeframes = listOf("1H", "24H", "7D", "30D", "1Y")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CRYPTO ASSET RADAR",
                        color = CyanNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "BTC / USD Surveillance",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Price",
                        tint = CyanNeon
                    )
                }
            }
        }

        // Real-Time Bitcoin Retrofit Market Card with Neon Cyan Trend Line
        item {
            NeonCyanTrendLineCard(
                asset = btc,
                chartHeight = 175.dp,
                lowEffectsMode = uiState.config.lowEffectsMode,
                onRefreshClick = onRefresh
            )
        }

        // Technical Indicators Panel
        item {
            HolographicGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "NEURAL TECHNICAL INDICATORS",
                        color = CyanNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // RSI (14)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x330B172E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("RSI (14)", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text("63.8", color = CyanNeon, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text("Bullish Momentum", color = Color(0xFF00E676), fontSize = 10.sp)
                            }
                        }

                        // MACD
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x330B172E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("MACD (12, 26, 9)", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text("+412.5", color = Color(0xFF00E676), fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text("Golden Cross", color = CyanNeon, fontSize = 10.sp)
                            }
                        }

                        // EMA 200
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x330B172E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("200 EMA", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text("Above Trend", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text("Strong Support", color = TextCyan, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // Price Alert Manager
        item {
            HolographicGlassCard(
                borderColor = GlassBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Alerts",
                                tint = CrimsonWeb,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PRICE ALERTS & TRIGGERS",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Button(
                            onClick = { showAddAlertSheet = !showAddAlertSheet },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0x33FF1744),
                                contentColor = CrimsonWeb
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_alert_btn")
                        ) {
                            Icon(Icons.Default.AddAlert, contentDescription = "Add", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Alert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Add alert form
                    if (showAddAlertSheet) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = targetPriceInput,
                                onValueChange = { targetPriceInput = it },
                                placeholder = { Text("Price (e.g. 70000)", color = TextMuted, fontSize = 12.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("alert_price_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanNeon,
                                    unfocusedBorderColor = GlassBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    val price = targetPriceInput.toDoubleOrNull()
                                    if (price != null) {
                                        onAddAlert("BTC", price, alertCondition)
                                        targetPriceInput = ""
                                        showAddAlertSheet = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Save", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Alerts list
                    uiState.priceAlerts.forEach { alert ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (alert.isTriggered) CrimsonWeb else Color(0xFF00E676))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${alert.symbol} ${if (alert.condition == AlertCondition.ABOVE) ">=" else "<="} $${String.format("%,.0f", alert.targetPrice)}",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                if (alert.isTriggered) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = CrimsonGlow
                                    ) {
                                        Text(
                                            text = "TRIGGERED",
                                            color = CrimsonWeb,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { onDeleteAlert(alert.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Quick MT5 Trade Dispatcher
        item {
            HolographicGlassCard(
                borderColor = CyanNeon,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "DIRECT MT5 TERMINAL EXECUTION",
                        color = CyanNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Dispatch immediate market orders directly to MT5 terminal at 127.0.0.1:8222 with 1.0% risk parameter.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onQuickTrade("BUY", 0.05) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676), contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("buy_btc_btn")
                        ) {
                            Text("BUY 0.05 BTC", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { onQuickTrade("SELL", 0.05) },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonWeb, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sell_btc_btn")
                        ) {
                            Text("SELL 0.05 BTC", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
