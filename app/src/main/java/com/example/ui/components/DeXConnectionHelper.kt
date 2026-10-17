package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonWeb
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceBright
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.MidnightNavyLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

/**
 * Interactive step-by-step tutorial on how to enable Samsung Wireless DeX via Quick Settings.
 */
@Composable
fun DeXConnectionHelper(
    onDismiss: () -> Unit,
    onActivateDesktopMode: () -> Unit,
    modifier: Modifier = Modifier,
    initialStep: Int = 1
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(initialStep.coerceIn(1, 5)) }
    var selectedTab by remember { mutableStateOf("TUTORIAL") } // TUTORIAL, QUICK_REF, TROUBLESHOOT
    
    // Interactive step simulation states
    var shadeSimulatedExpanded by remember { mutableStateOf(false) }
    var dexTileToggled by remember { mutableStateOf(false) }
    var selectedProjectionMode by remember { mutableStateOf("TV_OR_MONITOR") } // TV_OR_MONITOR or PC_WIRELESS
    var isScanningDevices by remember { mutableStateOf(false) }
    var connectedDeviceName by remember { mutableStateOf<String?>("Smart Monitor M8 (Workspace)") }
    var showMissingTileHelp by remember { mutableStateOf(false) }
    var completedSteps by remember { mutableStateOf(setOf<Int>()) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MidnightNavy,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, CyanNeon),
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp)
            .testTag("dex_connection_helper")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header with Holographic JARVIS Styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = CyanGlow,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CastConnected,
                                contentDescription = "DeX Wireless",
                                tint = CyanNeon,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SAMSUNG WIRELESS DeX",
                                color = CyanNeon,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CyanGlow,
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, CyanNeon)
                            ) {
                                Text(
                                    text = "STEP $currentStep OF 5",
                                    color = CyanNeon,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Interactive Quick Settings Tutorial",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("close_dex_help_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs (Tutorial vs Quick Ref vs Troubleshoot)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x330E1A30), RoundedCornerShape(8.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabButton(
                    title = "Interactive Tutorial",
                    icon = Icons.Default.TouchApp,
                    isSelected = selectedTab == "TUTORIAL",
                    onClick = { selectedTab = "TUTORIAL" },
                    modifier = Modifier.weight(1f).testTag("dex_tab_tutorial")
                )
                TabButton(
                    title = "Quick Guide",
                    icon = Icons.Default.DesktopWindows,
                    isSelected = selectedTab == "QUICK_REF",
                    onClick = { selectedTab = "QUICK_REF" },
                    modifier = Modifier.weight(1f).testTag("dex_tab_quick_ref")
                )
                TabButton(
                    title = "Troubleshoot",
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    isSelected = selectedTab == "TROUBLESHOOT",
                    onClick = { selectedTab = "TROUBLESHOOT" },
                    modifier = Modifier.weight(1f).testTag("dex_tab_troubleshoot")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == "TUTORIAL") {
                // Step Progress Indicator pinned at top
                StepProgressBar(
                    currentStep = currentStep,
                    completedSteps = completedSteps,
                    onStepSelected = { step -> currentStep = step }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Scrollable Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTab) {
                    "TUTORIAL" -> {

                    // Step 1: Open Galaxy Quick Settings
                    if (currentStep == 1) {
                        Step1QuickSettingsView(
                            shadeExpanded = shadeSimulatedExpanded,
                            onToggleShade = {
                                shadeSimulatedExpanded = !shadeSimulatedExpanded
                                if (shadeSimulatedExpanded) {
                                    completedSteps = completedSteps + 1
                                }
                            },
                            onOpenSystemSettings = {
                                openSystemCastSettings(context)
                            }
                        )
                    }

                    // Step 2: Tap the 'DeX' Quick Tile
                    if (currentStep == 2) {
                        Step2DexTileView(
                            isDeXToggled = dexTileToggled,
                            onToggleDeXTile = {
                                dexTileToggled = !dexTileToggled
                                if (dexTileToggled) {
                                    completedSteps = completedSteps + 2
                                }
                            },
                            showMissingTileHelp = showMissingTileHelp,
                            onToggleMissingTileHelp = { showMissingTileHelp = !showMissingTileHelp }
                        )
                    }

                    // Step 3: Select Wireless Projection Mode
                    if (currentStep == 3) {
                        Step3ProjectionModeView(
                            selectedMode = selectedProjectionMode,
                            onSelectMode = { mode ->
                                selectedProjectionMode = mode
                                completedSteps = completedSteps + 3
                            }
                        )
                    }

                    // Step 4: Choose Target Display & Start Now
                    if (currentStep == 4) {
                        Step4TargetDisplayView(
                            isScanning = isScanningDevices,
                            onRefreshScan = { isScanningDevices = !isScanningDevices },
                            connectedDevice = connectedDeviceName,
                            onSelectDevice = { name ->
                                connectedDeviceName = name
                                completedSteps = completedSteps + 4
                            }
                        )
                    }

                    // Step 5: Start Desktop Mode & Summary
                    if (currentStep == 5) {
                        Step5DesktopReadyView(
                            connectedDevice = connectedDeviceName ?: "Wireless DeX Display",
                            onLaunchDesktop = {
                                completedSteps = completedSteps + 5
                                onActivateDesktopMode()
                                onDismiss()
                            },
                            onOpenSystemSettings = {
                                openSystemCastSettings(context)
                            },
                            onRestart = {
                                currentStep = 1
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Bottom Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (currentStep > 1) currentStep -= 1
                            },
                            enabled = currentStep > 1,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextCyan
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (currentStep > 1) CyanNeon.copy(alpha = 0.5f) else Color.Transparent
                            ),
                            modifier = Modifier.testTag("dex_prev_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Previous", fontSize = 12.sp)
                        }

                        if (currentStep < 5) {
                            Button(
                                onClick = {
                                    completedSteps = completedSteps + currentStep
                                    currentStep += 1
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyanNeon,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier.testTag("dex_next_button")
                            ) {
                                Text(
                                    text = "Next Step (${currentStep + 1}/5)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    onActivateDesktopMode()
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyanNeon,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier.testTag("activate_desktop_mode_btn")
                            ) {
                                Text(
                                    text = "Launch Desktop UI",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                "QUICK_REF" -> {
                    QuickReferenceView(onActivateDesktopMode = {
                        onActivateDesktopMode()
                        onDismiss()
                    })
                }

                "TROUBLESHOOT" -> {
                    TroubleshootView(onOpenSystemSettings = {
                        openSystemCastSettings(context)
                    })
                }
            }
        }
    }
}
}

/**
 * Step Progress Bar with numbered interactive circles.
 */
@Composable
private fun StepProgressBar(
    currentStep: Int,
    completedSteps: Set<Int>,
    onStepSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 1..5) {
            val isCurrent = step == currentStep
            val isCompleted = completedSteps.contains(step)

            val circleColor = when {
                isCurrent -> CyanNeon
                isCompleted -> SuccessGreen
                else -> Color(0x33101E38)
            }
            val textColor = when {
                isCurrent -> Color.Black
                isCompleted -> Color.Black
                else -> TextMuted
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onStepSelected(step) }
                    .testTag("dex_step_tab_$step")
            ) {
                Surface(
                    shape = CircleShape,
                    color = circleColor,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCurrent) CyanNeon else GlassBorder
                    ),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isCompleted && !isCurrent) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "$step",
                                color = textColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = when (step) {
                        1 -> "Swipe"
                        2 -> "DeX Tile"
                        3 -> "Wireless"
                        4 -> "Pair"
                        else -> "Launch"
                    },
                    color = if (isCurrent) CyanNeon else TextMuted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                )
            }

            if (step < 5) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            if (completedSteps.contains(step)) SuccessGreen.copy(alpha = 0.7f)
                            else Color(0x3300F0FF)
                        )
                )
            }
        }
    }
}

/**
 * Step 1: Open Galaxy Quick Settings shade
 */
@Composable
private fun Step1QuickSettingsView(
    shadeExpanded: Boolean,
    onToggleShade: () -> Unit,
    onOpenSystemSettings: () -> Unit
) {
    Column {
        Text(
            text = "STEP 1: REVEAL GALAXY QUICK SETTINGS",
            color = CyanNeon,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Swipe down twice from the top edge of your Samsung Galaxy display (or once using two fingers) to expand the full Quick Settings matrix.",
            color = TextWhite,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive simulation canvas / card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0x330C162A),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Swipe Down Simulator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GALAXY STATUS BAR SIMULATION",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "12:00 PM • 100% 5G",
                        color = TextCyan,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (shadeExpanded) Color(0x4400F0FF) else Color(0x2213233E),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (shadeExpanded) CyanNeon else GlassBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleShade() }
                        .testTag("simulate_swipe_down_btn")
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            imageVector = if (shadeExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Swipe indicator",
                            tint = if (shadeExpanded) CyanNeon else TextMuted,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (shadeExpanded) "Quick Settings Shade Expanded! (Tap to close)" else "TAP OR SWIPE TO SIMULATE SWIPE DOWN",
                            color = if (shadeExpanded) CyanNeon else TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                AnimatedVisibility(
                    visible = shadeExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x3300E676),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Shade opened! Advance to Step 2 to locate the DeX tile.",
                                    color = TextWhite,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Direct Android System Settings Button
        OutlinedButton(
            onClick = onOpenSystemSettings,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextCyan
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dex_open_system_settings_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Open Settings",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Device Wireless Projection Settings", fontSize = 11.sp)
        }
    }
}

/**
 * Step 2: Locate and Tap the 'DeX' Quick Tile
 */
@Composable
private fun Step2DexTileView(
    isDeXToggled: Boolean,
    onToggleDeXTile: () -> Unit,
    showMissingTileHelp: Boolean,
    onToggleMissingTileHelp: () -> Unit
) {
    Column {
        Text(
            text = "STEP 2: TAP THE 'DeX' QUICK TILE",
            color = CyanNeon,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "In the expanded Quick Settings grid, look for the 'DeX' tile. Tap it to toggle Wireless DeX on, or long-press it to open Samsung DeX settings.",
            color = TextWhite,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Simulated Quick Settings Grid
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0x330B172E),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "SIMULATED QUICK SETTINGS MATRIX (ONE UI)",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickTileItem(
                        title = "Wi-Fi",
                        icon = Icons.Default.Wifi,
                        isActive = true,
                        onClick = {}
                    )
                    QuickTileItem(
                        title = "Sound",
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        isActive = true,
                        onClick = {}
                    )
                    QuickTileItem(
                        title = "Bluetooth",
                        icon = Icons.Default.DesktopWindows,
                        isActive = true,
                        onClick = {}
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Interactive DeX Tile!
                    QuickTileItem(
                        title = "DeX",
                        icon = Icons.Default.DesktopWindows,
                        isActive = isDeXToggled,
                        isTarget = true,
                        onClick = onToggleDeXTile,
                        modifier = Modifier.testTag("dex_quick_settings_tile")
                    )
                    QuickTileItem(
                        title = "Torch",
                        icon = Icons.Default.FlashlightOn,
                        isActive = false,
                        onClick = {}
                    )
                    QuickTileItem(
                        title = "Smart View",
                        icon = Icons.Default.Tv,
                        isActive = false,
                        onClick = {}
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isDeXToggled) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x3300E676),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "DeX Active",
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Samsung DeX toggled ON! Proceed to Step 3 to select screen mode.",
                                color = TextWhite,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = "• Tap the glowing 'DeX' tile above to test interactive activation",
                        color = TextCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Missing DeX Tile Accordion
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0x2213233E),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleMissingTileHelp() }
                .testTag("missing_dex_tile_help_toggle")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "Help",
                            tint = AmberAlert,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Can't find the 'DeX' tile?",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        imageVector = if (showMissingTileHelp) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (showMissingTileHelp) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Swipe right to view page 2 or 3 of your Quick Settings.\n2. Tap the '+' button (or 3 dots > 'Edit buttons' in One UI).\n3. Drag the 'DeX' icon down into your active Quick Settings grid and tap Done.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Step 3: Select Wireless Projection Mode
 */
@Composable
private fun Step3ProjectionModeView(
    selectedMode: String,
    onSelectMode: (String) -> Unit
) {
    Column {
        Text(
            text = "STEP 3: CHOOSE WIRELESS PROJECTION",
            color = CyanNeon,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Samsung DeX presents options to connect either wirelessly to a TV/Monitor or to a PC companion app. Select your preferred target.",
            color = TextWhite,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Mode 1: TV or Monitor (Wireless Miracast)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (selectedMode == "TV_OR_MONITOR") Color(0x4400F0FF) else Color(0x2213233E),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (selectedMode == "TV_OR_MONITOR") CyanNeon else GlassBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectMode("TV_OR_MONITOR") }
                .testTag("mode_tv_monitor_btn")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (selectedMode == "TV_OR_MONITOR") CyanNeon else Color(0x3300F0FF),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "TV",
                            tint = if (selectedMode == "TV_OR_MONITOR") Color.Black else CyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "DeX on TV or Monitor (Wireless)",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = SuccessGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(0.6.dp, SuccessGreen)
                        ) {
                            Text(
                                text = "RECOMMENDED",
                                color = SuccessGreen,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Projects full 1080p/4K desktop wirelessly via Miracast / Wi-Fi Direct. No PC or cables needed.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                if (selectedMode == "TV_OR_MONITOR") {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = CyanNeon,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Mode 2: DeX on PC (Windows 10/11)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (selectedMode == "PC_WIRELESS") Color(0x4400F0FF) else Color(0x2213233E),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (selectedMode == "PC_WIRELESS") CyanNeon else GlassBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectMode("PC_WIRELESS") }
                .testTag("mode_pc_wireless_btn")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (selectedMode == "PC_WIRELESS") CyanNeon else Color(0x3300F0FF),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Computer,
                            contentDescription = "PC",
                            tint = if (selectedMode == "PC_WIRELESS") Color.Black else CyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DeX on PC (Windows 10 / 11)",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Connects to Windows PC with 'Samsung DeX for PC' app running over the same Wi-Fi network.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                if (selectedMode == "PC_WIRELESS") {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = CyanNeon,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Optimization Tip
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0x3300E676),
            border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Low Latency",
                    tint = SuccessGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pro Tip: 5GHz Wi-Fi delivers <15ms latency, ensuring smooth 60fps charting and responsive gesture tracking.",
                    color = TextWhite,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * Step 4: Choose Target Display and Pair
 */
@Composable
private fun Step4TargetDisplayView(
    isScanning: Boolean,
    onRefreshScan: () -> Unit,
    connectedDevice: String?,
    onSelectDevice: (String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_spin"
    )

    Column {
        Text(
            text = "STEP 4: SELECT TARGET DISPLAY & 'START NOW'",
            color = CyanNeon,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Your Galaxy phone will discover Miracast-compatible screens. Select your display from the list and tap 'Start now' when prompted.",
            color = TextWhite,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Device discovery box
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0x330C162A),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Router,
                            contentDescription = "Radar",
                            tint = CyanNeon,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(if (isScanning) radarAngle else 0f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AVAILABLE WIRELESS DISPLAYS",
                            color = CyanNeon,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    IconButton(
                        onClick = onRefreshScan,
                        modifier = Modifier.size(28.dp).testTag("dex_rescan_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = TextCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Devices
                val devices = listOf(
                    Triple("Smart Monitor M8 (Workspace)", "Low Latency • 5GHz Direct", true),
                    Triple("Samsung Neo QLED 65\" (Living Room)", "Miracast Ready • 4K UHD", false),
                    Triple("Windows 10/11 DeX Host PC", "Samsung DeX for PC v3.2", false)
                )

                devices.forEachIndexed { index, (name, specs, isDefault) ->
                    val isSelected = connectedDevice == name

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0x4400F0FF) else Color(0x2213233E),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CyanNeon else GlassBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectDevice(name) }
                            .testTag("dex_device_item_$index")
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (name.contains("PC")) Icons.Default.Computer else Icons.Default.Tv,
                                contentDescription = "Device",
                                tint = if (isSelected) CyanNeon else TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = specs,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                            if (isSelected) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = SuccessGreen.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, SuccessGreen)
                                ) {
                                    Text(
                                        text = "PAIRED",
                                        color = SuccessGreen,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "• Tap any display above to establish simulated pairing handshake.",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

/**
 * Step 5: Start Desktop Mode & Summary
 */
@Composable
private fun Step5DesktopReadyView(
    connectedDevice: String,
    onLaunchDesktop: () -> Unit,
    onOpenSystemSettings: () -> Unit,
    onRestart: () -> Unit
) {
    Column {
        Text(
            text = "STEP 5: LAUNCH JARVIS DESKTOP WORKSPACE",
            color = CyanNeon,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Samsung Wireless DeX is now projecting. Launch the side-by-side Desktop Workspace to view live BTC analytics, ARACHNE AI terminal, and Holographic Bloom.",
            color = TextWhite,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Connection Summary Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0x330B172E),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CyanNeon),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = SuccessGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Active",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "WIRELESS DeX ACTIVE",
                                color = SuccessGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = connectedDevice,
                                color = TextWhite,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "1080p @ 60FPS",
                        color = CyanNeon,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Diagnostic Telemetry row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TelemetryMiniBadge(label = "INPUT PING", value = "12ms", color = SuccessGreen)
                    TelemetryMiniBadge(label = "TOUCHPAD", value = "ENABLED", color = CyanNeon)
                    TelemetryMiniBadge(label = "MULTI-PANEL", value = "3-COLUMN", color = CyanNeon)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary CTA
                Button(
                    onClick = onLaunchDesktop,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanNeon,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dex_launch_desktop_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.DesktopWindows,
                        contentDescription = "Launch",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "OPEN DESKTOP WORKSPACE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenSystemSettings,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.3f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("System Settings", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onRestart,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.weight(1f).testTag("dex_restart_tutorial_btn")
                    ) {
                        Text("Replay Tutorial", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

/**
 * Tab 2: Quick Reference view
 */
@Composable
private fun QuickReferenceView(onActivateDesktopMode: () -> Unit) {
    Column {
        Text(
            text = "SAMSUNG DeX QUICK REFERENCE SUMMARY",
            color = CyanNeon,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(8.dp))

        val steps = listOf(
            Triple("01", "Swipe Down Twice", "Reveal full Galaxy Quick Settings shade from top of screen."),
            Triple("02", "Tap 'DeX' Tile", "Tap DeX in the quick toggle matrix to start wireless projection search."),
            Triple("03", "Select Display", "Pick 'DeX on TV or Monitor' and tap your target display."),
            Triple("04", "Confirm 'Start Now'", "Accept projection prompt on phone to establish Miracast link."),
            Triple("05", "Phone as Touchpad", "Tap 'Use your phone as a touchpad' in the notification panel.")
        )

        steps.forEach { (num, title, desc) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0x330B172E),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.4f)),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = num,
                            color = CyanNeon,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = desc,
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onActivateDesktopMode,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanNeon,
                contentColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth().testTag("quick_ref_switch_btn")
        ) {
            Text("Switch to Desktop UI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Tab 3: Troubleshooting FAQs
 */
@Composable
private fun TroubleshootView(onOpenSystemSettings: () -> Unit) {
    Column {
        Text(
            text = "TROUBLESHOOTING & HARDWARE COMPATIBILITY",
            color = CyanNeon,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(10.dp))

        TroubleshootItem(
            question = "DeX tile does not appear in Quick Settings?",
            answer = "Open Quick Settings, swipe to the last page, tap '+' (or 3-dots > Edit buttons). Find 'DeX' in the top section and drag it into your active buttons."
        )

        TroubleshootItem(
            question = "Display or TV is not detected?",
            answer = "Ensure both your Galaxy phone and TV are connected to the same Wi-Fi router (preferably 5GHz). Also ensure screen mirroring/Miracast is enabled on your TV settings."
        )

        TroubleshootItem(
            question = "High latency or stuttering on 4GB RAM devices?",
            answer = "Turn off high-bandwidth downloads in the background. Connect via 5GHz Wi-Fi or directly via USB-C to HDMI adapter for latency under 10ms."
        )

        TroubleshootItem(
            question = "Connecting to Windows 10/11 PC?",
            answer = "Download 'Samsung DeX for PC' from Samsung's official site. Ensure your PC is running and the DeX app is open on Windows before pairing."
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenSystemSettings,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextCyan),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Android Cast / Connection Settings", fontSize = 11.sp)
        }
    }
}

@Composable
private fun TroubleshootItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0x2213233E),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = answer,
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun QuickTileItem(
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    isTarget: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isTarget) CyanNeon else if (isActive) CyanNeon.copy(alpha = 0.5f) else GlassBorder
    val surfaceColor = if (isActive) CyanNeon else Color(0x33101E38)
    val iconColor = if (isActive) Color.Black else TextMuted

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(80.dp)
            .clickable { onClick() }
    ) {
        Surface(
            shape = CircleShape,
            color = surfaceColor,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
            modifier = Modifier.size(46.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = if (isActive) CyanNeon else TextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun TelemetryMiniBadge(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0x330A1424),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = TextMuted,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = value,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) CyanNeon else Color.Transparent,
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.Black else TextMuted,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                color = if (isSelected) Color.Black else TextMuted,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Helper dialog wrapping DeXConnectionHelper
 */
@Composable
fun DeXConnectionHelperDialog(
    onDismiss: () -> Unit,
    onActivateDesktopMode: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        DeXConnectionHelper(
            onDismiss = onDismiss,
            onActivateDesktopMode = onActivateDesktopMode,
            modifier = Modifier.testTag("dex_connection_help_dialog")
        )
    }
}

/**
 * Safely open Android system cast or wireless settings intent
 */
private fun openSystemCastSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_CAST_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                // Ignore if in test environment or restricted
            }
        }
    }
}
