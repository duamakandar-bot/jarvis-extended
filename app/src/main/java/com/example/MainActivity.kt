package com.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.viewmodel.JarvisUiState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DexConnectionHelpDialog
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AutomationsScreen
import com.example.ui.screens.BtcMarketsScreen
import com.example.ui.screens.DesktopWorkspace
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SystemScreen
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonWeb
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceBright
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.viewmodel.JarvisScreen
import com.example.viewmodel.JarvisViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                JarvisCommandApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JarvisCommandApp(viewModel: JarvisViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsStateWithLifecycle()

    // Speech recognition launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.sendChatMessage(spokenText, isVoice = true)
                viewModel.navigateTo(JarvisScreen.AI_ASSISTANT)
            }
        }
    }

    val launchSpeech: () -> Unit = {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "ARACHNE is listening. Speak your tactical command...")
            }
            speechLauncher.launch(intent)
        } catch (_: Exception) {
            viewModel.navigateTo(JarvisScreen.AI_ASSISTANT)
        }
    }

    // Samsung Wireless DeX Help Dialog
    if (uiState.showDexHelpDialog) {
        DexConnectionHelpDialog(
            onDismiss = { viewModel.setDexHelpDialogVisible(false) },
            onActivateDesktopMode = { viewModel.toggleDexMode(true) }
        )
    }

    // Hardware back navigation
    if (uiState.isDexMode) {
        BackHandler {
            viewModel.toggleDexMode(false)
        }
    } else if (uiState.currentScreen != JarvisScreen.HOME) {
        BackHandler {
            viewModel.navigateTo(JarvisScreen.HOME)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(MidnightNavy)) {
        val isWideScreen = maxWidth >= 600.dp
        val showDesktopWorkspace = uiState.isDexMode || (isWideScreen && uiState.currentScreen == JarvisScreen.HOME)

        if (showDesktopWorkspace) {
            DesktopWorkspace(
                uiState = uiState,
                isSpeaking = isSpeaking,
                onSendMessage = { viewModel.sendChatMessage(it) },
                onLaunchSpeech = launchSpeech,
                onToggleVoiceTts = { viewModel.toggleVoiceTts(it) },
                onStopSpeech = { viewModel.voiceManager.stop() },
                onRefreshCrypto = { viewModel.refreshCryptoData() },
                onAddAlert = { sym, price, cond -> viewModel.addPriceAlert(sym, price, cond) },
                onDeleteAlert = { viewModel.deletePriceAlert(it) },
                onQuickTrade = { type, lots -> viewModel.executeMt5Trade(type, lots) },
                onToggleWorkflow = { viewModel.toggleAutomation(it) },
                onOptimizeMemory = { viewModel.optimizeMemory() },
                onTriggerDiagnostics = { viewModel.triggerHardwareDiagnosticsPulse() },
                onToggleDexMode = { viewModel.toggleDexMode() },
                onOpenDexHelp = { viewModel.setDexHelpDialogVisible(true) },
                onDismissDexHelp = { viewModel.setDexHelpDialogVisible(false) }
            )
        } else if (isWideScreen) {
            // Tablet / Desktop Dual-Mode Layout with Holographic Navigation Rail
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = Color(0xFF090E1B),
                    contentColor = TextWhite,
                    header = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_jarvis_logo),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, CyanNeon, CircleShape)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "JARVIS",
                                color = CyanNeon,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    },
                    modifier = Modifier.fillMaxHeight()
                ) {
                    val navItems = listOf(
                        Triple(JarvisScreen.HOME, "Home", Icons.Default.Home),
                        Triple(JarvisScreen.AI_ASSISTANT, "Assistant", Icons.Default.Psychology),
                        Triple(JarvisScreen.BTC_MARKETS, "Markets", Icons.AutoMirrored.Filled.TrendingUp),
                        Triple(JarvisScreen.AUTOMATIONS, "Automations", Icons.Default.Hub),
                        Triple(JarvisScreen.SYSTEM, "System", Icons.Default.Computer),
                        Triple(JarvisScreen.SETTINGS, "Settings", Icons.Default.Settings)
                    )

                    navItems.forEach { (screen, label, icon) ->
                        NavigationRailItem(
                            selected = uiState.currentScreen == screen,
                            onClick = { viewModel.navigateTo(screen) },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = CyanNeon,
                                indicatorColor = CyanNeon,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }

                // Main Content for Wide Screen
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = when (uiState.currentScreen) {
                                        JarvisScreen.HOME -> "Spider Command Center"
                                        JarvisScreen.AI_ASSISTANT -> "ARACHNE AI Assistant"
                                        JarvisScreen.BTC_MARKETS -> "BTC Market Radar"
                                        JarvisScreen.AUTOMATIONS -> "Automation Matrix"
                                        JarvisScreen.SYSTEM -> "Hardware Telemetry"
                                        JarvisScreen.SETTINGS -> "System Settings"
                                    },
                                    color = TextWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            actions = {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x330A192F),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .clickable { viewModel.setDexHelpDialogVisible(true) }
                                        .padding(end = 6.dp)
                                        .testTag("connect_dex_btn")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.CastConnected, contentDescription = "DeX", tint = CyanNeon, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Connect DeX", color = CyanNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (uiState.isDexMode) CyanGlow else Color(0x330B172E),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon),
                                    modifier = Modifier
                                        .clickable { viewModel.toggleDexMode() }
                                        .padding(end = 8.dp)
                                        .testTag("desktop_dex_toggle_btn")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.DesktopWindows, contentDescription = "DeX Mode", tint = CyanNeon, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (uiState.isDexMode) "DeX ON" else "DeX Mode", color = CyanNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFF070B14)
                            )
                        )
                    },
                    containerColor = MidnightNavy,
                    modifier = Modifier.weight(1f)
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        ScreenDispatcher(
                            uiState = uiState,
                            isSpeaking = isSpeaking,
                            viewModel = viewModel,
                            onLaunchSpeech = launchSpeech
                        )
                    }
                }
            }
        } else {
            // Handheld Mobile Layout with Bottom Navigation Bar
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_jarvis_logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, CyanNeon, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (uiState.currentScreen) {
                                        JarvisScreen.HOME -> "J.A.R.V.I.S."
                                        JarvisScreen.AI_ASSISTANT -> "ARACHNE AI"
                                        JarvisScreen.BTC_MARKETS -> "BTC Radar"
                                        JarvisScreen.AUTOMATIONS -> "Automations"
                                        JarvisScreen.SYSTEM -> "System"
                                        JarvisScreen.SETTINGS -> "Settings"
                                    },
                                    color = TextWhite,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        },
                        navigationIcon = {
                            if (uiState.currentScreen != JarvisScreen.HOME) {
                                IconButton(onClick = { viewModel.navigateTo(JarvisScreen.HOME) }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = CyanNeon
                                    )
                                }
                            }
                        },
                        actions = {
                            // Connect to DeX Help action
                            IconButton(
                                onClick = { viewModel.setDexHelpDialogVisible(true) },
                                modifier = Modifier.size(32.dp).testTag("mobile_dex_help_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CastConnected,
                                    contentDescription = "Connect to DeX",
                                    tint = CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Manual DeX Mode toggle button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (uiState.isDexMode) CyanGlow else Color(0x330B172E),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon),
                                modifier = Modifier
                                    .clickable { viewModel.toggleDexMode() }
                                    .padding(end = 6.dp)
                                    .testTag("mobile_dex_toggle_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.DesktopWindows, contentDescription = "DeX Mode", tint = CyanNeon, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("DeX", color = CyanNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (uiState.config.lowEffectsMode) Color(0x3300E676) else CyanGlow,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (uiState.config.lowEffectsMode) Color(0xFF00E676) else CyanNeon),
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Text(
                                    text = if (uiState.config.lowEffectsMode) "4GB LOW-RAM" else "HOLO-FX",
                                    color = if (uiState.config.lowEffectsMode) Color(0xFF00E676) else CyanNeon,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MidnightNavy
                        ),
                        modifier = Modifier.statusBarsPadding()
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = Color(0xFF090E1B),
                        contentColor = TextWhite,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .border(androidx.compose.foundation.BorderStroke(1.dp, GlassBorder))
                            .testTag("bottom_nav_bar")
                    ) {
                        val items = listOf(
                            Triple(JarvisScreen.HOME, "Home", Icons.Default.Home),
                            Triple(JarvisScreen.AI_ASSISTANT, "Assistant", Icons.Default.Psychology),
                            Triple(JarvisScreen.BTC_MARKETS, "Markets", Icons.AutoMirrored.Filled.TrendingUp),
                            Triple(JarvisScreen.AUTOMATIONS, "Auto", Icons.Default.Hub),
                            Triple(JarvisScreen.SYSTEM, "System", Icons.Default.Computer),
                            Triple(JarvisScreen.SETTINGS, "Config", Icons.Default.Settings)
                        )

                        items.forEach { (screen, label, icon) ->
                            NavigationBarItem(
                                selected = uiState.currentScreen == screen,
                                onClick = { viewModel.navigateTo(screen) },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (uiState.currentScreen == screen) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = CyanNeon,
                                    indicatorColor = CyanNeon,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                            )
                        }
                    }
                },
                containerColor = MidnightNavy
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ScreenDispatcher(
                        uiState = uiState,
                        isSpeaking = isSpeaking,
                        viewModel = viewModel,
                        onLaunchSpeech = launchSpeech
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenDispatcher(
    uiState: JarvisUiState,
    isSpeaking: Boolean,
    viewModel: JarvisViewModel,
    onLaunchSpeech: () -> Unit
) {
    when (uiState.currentScreen) {
        JarvisScreen.HOME -> HomeScreen(
            uiState = uiState,
            isSpeaking = isSpeaking,
            onNavigate = { viewModel.navigateTo(it) },
            onOptimizeMemory = { viewModel.optimizeMemory() },
            onQuickVoiceTrigger = onLaunchSpeech
        )
        JarvisScreen.AI_ASSISTANT -> AiAssistantScreen(
            uiState = uiState,
            isSpeaking = isSpeaking,
            onSendMessage = { viewModel.sendChatMessage(it) },
            onVoiceClick = onLaunchSpeech,
            onToggleVoiceTts = { viewModel.toggleVoiceTts(it) },
            onStopSpeech = { viewModel.voiceManager.stop() }
        )
        JarvisScreen.BTC_MARKETS -> BtcMarketsScreen(
            uiState = uiState,
            onRefresh = { viewModel.refreshCryptoData() },
            onAddAlert = { sym, price, cond -> viewModel.addPriceAlert(sym, price, cond) },
            onDeleteAlert = { viewModel.deletePriceAlert(it) },
            onQuickTrade = { type, lots -> viewModel.executeMt5Trade(type, lots) }
        )
        JarvisScreen.AUTOMATIONS -> AutomationsScreen(
            uiState = uiState,
            onToggleWorkflow = { viewModel.toggleAutomation(it) },
            onExecuteTrade = { type, lots -> viewModel.executeMt5Trade(type, lots) }
        )
        JarvisScreen.SYSTEM -> SystemScreen(
            uiState = uiState,
            onOptimizeMemory = { viewModel.optimizeMemory() },
            onDiagnosticsPulse = { viewModel.triggerHardwareDiagnosticsPulse() }
        )
        JarvisScreen.SETTINGS -> SettingsScreen(
            uiState = uiState,
            onToggleLowEffects = { viewModel.toggleLowEffectsMode(it) },
            onToggleVoiceTts = { viewModel.toggleVoiceTts(it) },
            onSpeechPitchChange = { viewModel.setSpeechPitch(it) },
            onSpeechSpeedChange = { viewModel.setSpeechSpeed(it) }
        )
    }
}
