package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.components.HolographicGlassCard
import com.example.ui.components.NeonBarTheme
import com.example.ui.components.NeonScanlineProgressBar
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CrimsonWeb
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceBright
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.viewmodel.JarvisUiState

enum class HardwareFilter {
    ALL,
    CPU_CORES,
    RAM_4GB,
    GPU_3D
}

@Composable
fun SystemScreen(
    uiState: JarvisUiState,
    onOptimizeMemory: () -> Unit,
    onDiagnosticsPulse: () -> Unit = {}
) {
    val telemetry = uiState.telemetry
    var selectedFilter by remember { mutableStateOf(HardwareFilter.ALL) }

    val ramRatio = telemetry.usedRamMb.toFloat() / telemetry.totalRamMb.toFloat()
    val gpuRatio = telemetry.gpuUsagePercent / 100f
    val cpuRatio = telemetry.cpuUsagePercent / 100f
    val vramRatio = telemetry.gpuVramUsedMb.toFloat() / telemetry.gpuVramTotalMb.toFloat()
    val ssdRatio = telemetry.storageUsedGb.toFloat() / telemetry.storageTotalGb.toFloat()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Master Dashboard Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HARDWARE DIAGNOSTICS // GLASS HUD",
                        color = CyanNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "System Telemetry Matrix",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x3300E676),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676))
                ) {
                    Text(
                        text = "ALL SENSORS ONLINE",
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Host Machine Identity Glass Card
        item {
            HolographicGlassCard(
                borderColor = CyanNeon,
                glowEffect = !uiState.config.lowEffectsMode,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = CyanGlow,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Computer,
                                        contentDescription = "Host",
                                        tint = CyanNeon,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "WINDOWS 10 (64-BIT) // DUAL BUILD",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = telemetry.processorName,
                                    color = TextCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GlassSurfaceBright,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                        ) {
                            Text(
                                text = "${telemetry.cpuTempC}°C",
                                color = if (telemetry.cpuTempC > 65) CrimsonWeb else Color(0xFF00E676),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("GRAPHICS ADAPTER", fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                            Text(telemetry.gpuName, fontSize = 11.sp, color = TextWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("ACTIVE COOLING", fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                            Text("${telemetry.fanSpeedRpm} RPM", fontSize = 11.sp, color = CyanNeon, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tactical Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onDiagnosticsPulse,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0x3300F0FF),
                                contentColor = CyanNeon
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_diagnostic_pulse")
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = "Pulse", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Diagnostics Pulse", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onOptimizeMemory,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0x33FF1744),
                                contentColor = CrimsonWeb
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonWeb),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_purge_ram")
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = "Purge", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (uiState.isOptimizingMemory) "Purging..." else "Purge RAM Heap",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Filter Chips for Focused Views
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(HardwareFilter.values()) { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) CyanNeon else Color(0x2213233D),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanNeon else GlassBorder),
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = when (filter) {
                                HardwareFilter.ALL -> "ALL DIAGNOSTICS"
                                HardwareFilter.CPU_CORES -> "CPU & CORES"
                                HardwareFilter.RAM_4GB -> "4GB RAM ENGINE"
                                HardwareFilter.GPU_3D -> "GPU 3D ENGINE"
                            },
                            color = if (isSelected) Color.Black else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 1. CPU USAGE WITH NEON SCANLINE PROGRESS BARS
        // ==========================================
        if (selectedFilter == HardwareFilter.ALL || selectedFilter == HardwareFilter.CPU_CORES) {
            item {
                HolographicGlassCard(
                    borderColor = CyanNeon,
                    glowEffect = !uiState.config.lowEffectsMode,
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
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "CPU",
                                    tint = CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CPU UTILIZATION // INTEL i5-5200U",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "2.20 GHz @ ${telemetry.cpuTempC}°C",
                                color = CyanNeon,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Master CPU Scanline Progress Bar
                        NeonScanlineProgressBar(
                            label = "TOTAL PROCESSOR LOAD",
                            progress = cpuRatio,
                            subLabel = "Package Power: 14.8W • Turbo Cap: 2.70 GHz",
                            barHeight = 18.dp,
                            barTheme = NeonBarTheme.AUTO_THRESHOLD,
                            lowEffectsMode = uiState.config.lowEffectsMode
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Individual Thread / Core Scanline Bars
                        Text(
                            text = "INDIVIDUAL CORE HARDWARE THREADS",
                            color = TextCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        telemetry.cpuCores.forEachIndexed { index, corePercent ->
                            NeonScanlineProgressBar(
                                label = "Core $index Thread",
                                progress = corePercent / 100f,
                                subLabel = "Frequency: 2,194 MHz • Voltage: 0.98V",
                                barHeight = 12.dp,
                                barTheme = if (corePercent > 80) NeonBarTheme.CRIMSON else NeonBarTheme.CYAN,
                                lowEffectsMode = uiState.config.lowEffectsMode,
                                showStatusBadge = false
                            )
                            if (index < telemetry.cpuCores.size - 1) {
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. RAM USAGE WITH NEON SCANLINE PROGRESS BARS
        // ==========================================
        if (selectedFilter == HardwareFilter.ALL || selectedFilter == HardwareFilter.RAM_4GB) {
            item {
                HolographicGlassCard(
                    borderColor = if (ramRatio > 0.8f) CrimsonWeb else CyanNeon,
                    glowEffect = !uiState.config.lowEffectsMode,
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
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = "RAM",
                                    tint = CrimsonWeb,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PHYSICAL MEMORY // 4 GB DDR3L",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "${telemetry.usedRamMb} MB / ${telemetry.totalRamMb} MB",
                                color = if (ramRatio > 0.8f) CrimsonWeb else CyanNeon,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Total RAM Scanline Progress Bar
                        NeonScanlineProgressBar(
                            label = "TOTAL SYSTEM RAM UTILIZATION",
                            progress = ramRatio,
                            subLabel = "Speed: 1600 MT/s • Free Headroom: ${telemetry.totalRamMb - telemetry.usedRamMb} MB",
                            barHeight = 18.dp,
                            barTheme = NeonBarTheme.AUTO_THRESHOLD,
                            lowEffectsMode = uiState.config.lowEffectsMode
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Memory Allocation Breakdown
                        Text(
                            text = "MEMORY SEGMENTATION BREAKDOWN",
                            color = TextCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // App Heap Bar
                        NeonScanlineProgressBar(
                            label = "JARVIS CORE RUNTIME HEAP",
                            progress = 112f / telemetry.totalRamMb.toFloat(),
                            subLabel = "Committed: 112 MB • Non-paged Pool",
                            barHeight = 12.dp,
                            barTheme = NeonBarTheme.CYAN,
                            lowEffectsMode = uiState.config.lowEffectsMode,
                            showStatusBadge = false
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // OS Cached Buffers Bar
                        NeonScanlineProgressBar(
                            label = "OS CACHED STANDBY BUFFERS",
                            progress = telemetry.ramCachedMb.toFloat() / telemetry.totalRamMb.toFloat(),
                            subLabel = "Cached: ${telemetry.ramCachedMb} MB • Purgeable",
                            barHeight = 12.dp,
                            barTheme = NeonBarTheme.AMBER,
                            lowEffectsMode = uiState.config.lowEffectsMode,
                            showStatusBadge = false
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Free Headroom Bar
                        val freeMb = telemetry.totalRamMb - telemetry.usedRamMb
                        NeonScanlineProgressBar(
                            label = "AVAILABLE FREE HEADROOM",
                            progress = freeMb.toFloat() / telemetry.totalRamMb.toFloat(),
                            subLabel = "Available: $freeMb MB • Ready for allocation",
                            barHeight = 12.dp,
                            barTheme = NeonBarTheme.CYAN,
                            lowEffectsMode = uiState.config.lowEffectsMode,
                            showStatusBadge = false
                        )
                    }
                }
            }
        }

        // ==========================================
        // 3. GPU USAGE WITH NEON SCANLINE PROGRESS BARS
        // ==========================================
        if (selectedFilter == HardwareFilter.ALL || selectedFilter == HardwareFilter.GPU_3D) {
            item {
                HolographicGlassCard(
                    borderColor = CrimsonWeb,
                    glowEffect = !uiState.config.lowEffectsMode,
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
                                    imageVector = Icons.Default.VideogameAsset,
                                    contentDescription = "GPU",
                                    tint = CrimsonWeb,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GPU 3D ENGINE // INTEL HD 5500",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "${telemetry.gpuClockMhz} MHz @ ${telemetry.gpuTempC}°C",
                                color = CrimsonWeb,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // GPU Core 3D Scanline Progress Bar
                        NeonScanlineProgressBar(
                            label = "GPU 3D GRAPHICS RENDER LOAD",
                            progress = gpuRatio,
                            subLabel = "DirectX 12 (FL 11_1) • Execution Units: 24 @ 900 MHz",
                            barHeight = 18.dp,
                            barTheme = NeonBarTheme.AUTO_THRESHOLD,
                            lowEffectsMode = uiState.config.lowEffectsMode
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Video Memory (VRAM) Scanline Progress Bar
                        NeonScanlineProgressBar(
                            label = "SHARED VIDEO MEMORY (VRAM)",
                            progress = vramRatio,
                            subLabel = "Allocated: ${telemetry.gpuVramUsedMb} MB / ${telemetry.gpuVramTotalMb} MB Dynamic",
                            barHeight = 14.dp,
                            barTheme = NeonBarTheme.CYAN,
                            lowEffectsMode = uiState.config.lowEffectsMode
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Hardware Acceleration:", color = TextMuted, fontSize = 11.sp)
                            Text("Direct3D / Metal Angle Active", color = Color(0xFF00E676), fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 4. STORAGE SSD & DISK I/O DIAGNOSTICS
        // ==========================================
        if (selectedFilter == HardwareFilter.ALL) {
            item {
                HolographicGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = "SSD",
                                    tint = CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SOLID STATE DRIVE (SSD) // 256 GB",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "${telemetry.storageUsedGb} GB / ${telemetry.storageTotalGb} GB",
                                color = CyanNeon,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // SSD Storage Scanline Bar
                        NeonScanlineProgressBar(
                            label = "SSD VOLUME OCCUPANCY",
                            progress = ssdRatio,
                            subLabel = "Read: 520 MB/s • Write: 480 MB/s • SMART: Healthy",
                            barHeight = 14.dp,
                            barTheme = NeonBarTheme.CYAN,
                            lowEffectsMode = uiState.config.lowEffectsMode
                        )
                    }
                }
            }

            // Real-Time Hardware Diagnostic Event Stream
            item {
                HolographicGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "LIVE HARDWARE EVENT STREAM",
                            color = CyanNeon,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("CPU Throttle Guard:", color = TextMuted, fontSize = 11.sp)
                            Text("UNTHROTTLED (Headroom +48°C)", color = Color(0xFF00E676), fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Low-Effects 4GB Guard:", color = TextMuted, fontSize = 11.sp)
                            Text(if (uiState.config.lowEffectsMode) "ENFORCED (Memory Safe)" else "HOLOGRAPHIC ACCELERATED", color = CyanNeon, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Network Socket Ping:", color = TextMuted, fontSize = 11.sp)
                            Text("${telemetry.networkPingMs} ms (Sub-30ms Optimal)", color = Color(0xFF00E676), fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
