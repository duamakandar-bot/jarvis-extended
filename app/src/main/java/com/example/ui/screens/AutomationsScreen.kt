package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.AutomationCategory
import com.example.model.InstagramMediaType
import com.example.model.PostStatus
import com.example.model.ScheduledPost
import com.example.ui.components.HolographicGlassCard
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

enum class AutomationViewTab {
    INSTAGRAM_CONTENT,
    MT5_TRADING,
    ALL_WORKFLOWS
}

@Composable
fun AutomationsScreen(
    uiState: JarvisUiState,
    onToggleWorkflow: (String) -> Unit,
    onExecuteTrade: (String, Double) -> Unit,
    onScheduleInstagramPost: (String, String, InstagramMediaType, String) -> Unit = { _, _, _, _ -> },
    onPublishInstagramNow: (String) -> Unit = {},
    onCancelScheduledPost: (String) -> Unit = {},
    onGenerateAiCaption: (String) -> String = { "" }
) {
    var selectedTab by remember { mutableStateOf(AutomationViewTab.INSTAGRAM_CONTENT) }
    var tradeLotSize by remember { mutableStateOf(0.05) }

    // Instagram Composer Form State
    var postCaption by remember {
        mutableStateOf("ARACHNE Intelligence Report: Automated neural scanning active. Market liquidity indicators aligned. 🕸️⚡ #JARVIS #AlgoTrading #AIAssistant")
    }
    var postMediaUrl by remember {
        mutableStateOf("https://images.unsplash.com/photo-1621416894569-0f39ed31d247?w=800")
    }
    var postMediaType by remember { mutableStateOf(InstagramMediaType.IMAGE) }
    var postScheduleTime by remember { mutableStateOf("Today at 18:30 GMT") }
    var showComposer by remember { mutableStateOf(true) }

    val presetImages = listOf(
        Pair("Crypto Radar", "https://images.unsplash.com/photo-1621416894569-0f39ed31d247?w=800"),
        Pair("Cyber HUD", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800"),
        Pair("Algo Signals", "https://images.unsplash.com/photo-1642543492481-44e81e3914a7?w=800")
    )

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
                        text = "AUTOMATION MATRIX",
                        color = CyanNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Meta Graph API & MT5 Hub",
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
                        text = "GRAPH API v19.0",
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Navigation Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedTab == AutomationViewTab.INSTAGRAM_CONTENT) CyanNeon else Color(0x33101E36),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedTab == AutomationViewTab.INSTAGRAM_CONTENT) CyanNeon else GlassBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = AutomationViewTab.INSTAGRAM_CONTENT }
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "INSTAGRAM",
                            color = if (selectedTab == AutomationViewTab.INSTAGRAM_CONTENT) Color.Black else TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedTab == AutomationViewTab.MT5_TRADING) CyanNeon else Color(0x33101E36),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedTab == AutomationViewTab.MT5_TRADING) CyanNeon else GlassBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = AutomationViewTab.MT5_TRADING }
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "MT5 BRIDGE",
                            color = if (selectedTab == AutomationViewTab.MT5_TRADING) Color.Black else TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedTab == AutomationViewTab.ALL_WORKFLOWS) CyanNeon else Color(0x33101E36),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedTab == AutomationViewTab.ALL_WORKFLOWS) CyanNeon else GlassBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = AutomationViewTab.ALL_WORKFLOWS }
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "WORKFLOWS",
                            color = if (selectedTab == AutomationViewTab.ALL_WORKFLOWS) Color.Black else TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // ==========================================
        // 1. INSTAGRAM CONTENT PUBLISHER & SCHEDULER
        // ==========================================
        if (selectedTab == AutomationViewTab.INSTAGRAM_CONTENT) {
            // Instagram Account Status Card
            item {
                val profile = uiState.igProfile
                HolographicGlassCard(
                    borderColor = CyanNeon,
                    glowEffect = !uiState.config.lowEffectsMode,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = CrimsonGlow,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = "Instagram",
                                        tint = CrimsonWeb,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "@${profile.username}",
                                    color = TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "${profile.followersCount} Followers • ${profile.mediaCount} Published Updates",
                                    color = TextCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0x3300E676),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFF00E676))
                        ) {
                            Text(
                                text = "AUTH VERIFIED",
                                color = Color(0xFF00E676),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Interactive Post Composer & Scheduler
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
                                    imageVector = Icons.Default.PostAdd,
                                    contentDescription = "Compose",
                                    tint = CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "COMPOSE & SCHEDULE UPDATE",
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Button(
                                onClick = {
                                    val aiText = onGenerateAiCaption("crypto btc update")
                                    if (aiText.isNotBlank()) postCaption = aiText
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0x3300F0FF),
                                    contentColor = CyanNeon
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_ai_caption")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "AI", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI Caption", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Media Type Selector Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InstagramMediaType.values().forEach { type ->
                                val isSelected = postMediaType == type
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) CyanNeon else Color(0x33101E36),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanNeon else GlassBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { postMediaType = type }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (type) {
                                                InstagramMediaType.IMAGE -> Icons.Default.Image
                                                InstagramMediaType.REELS -> Icons.Default.Movie
                                                InstagramMediaType.STORY -> Icons.Default.VideoLibrary
                                            },
                                            contentDescription = type.name,
                                            tint = if (isSelected) Color.Black else TextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = type.name,
                                            color = if (isSelected) Color.Black else TextMuted,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Media URL Input
                        Text("MEDIA URL (GRAPH API ASSET):", color = TextCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = postMediaUrl,
                            onValueChange = { postMediaUrl = it },
                            placeholder = { Text("Enter public HTTPS image or video URL...", color = TextMuted, fontSize = 11.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_media_url"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanNeon,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick Presets
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(presetImages) { (title, url) ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x2213233D),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, GlassBorder),
                                    modifier = Modifier.clickable { postMediaUrl = url }
                                ) {
                                    Text(
                                        text = "+ $title",
                                        color = TextCyan,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Caption Input
                        Text("POST CAPTION & HASHTAGS:", color = TextCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = postCaption,
                            onValueChange = { postCaption = it },
                            placeholder = { Text("Write content caption...", color = TextMuted, fontSize = 11.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(95.dp)
                                .testTag("input_caption"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanNeon,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Schedule Timing Pills
                        Text("SCHEDULE DISPATCH TIME:", color = TextCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Today at 18:30 GMT", "Tomorrow 09:00 GMT", "Immediate Dispatch").forEach { time ->
                                val isSelected = postScheduleTime == time
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0x33FF1744) else Color(0x2213233D),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CrimsonWeb else GlassBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { postScheduleTime = time }
                                ) {
                                    Text(
                                        text = time,
                                        color = if (isSelected) CrimsonWeb else TextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: Schedule or Publish Now
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (postCaption.isNotBlank() && postMediaUrl.isNotBlank()) {
                                        onScheduleInstagramPost(postCaption, postMediaUrl, postMediaType, postScheduleTime)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0x3300F0FF),
                                    contentColor = CyanNeon
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_schedule_post")
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = "Schedule", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Schedule Update", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    if (postCaption.isNotBlank() && postMediaUrl.isNotBlank()) {
                                        onScheduleInstagramPost(postCaption, postMediaUrl, postMediaType, "Immediate Dispatch")
                                        val latest = uiState.scheduledPosts.firstOrNull()
                                        if (latest != null) {
                                            onPublishInstagramNow(latest.id)
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CrimsonWeb,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_publish_direct")
                            ) {
                                if (uiState.isPublishingPost) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Publish", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Post Right Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Scheduled Updates Queue
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SCHEDULED CONTENT QUEUE",
                        color = CyanNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GlassSurfaceBright
                    ) {
                        Text(
                            text = "${uiState.scheduledPosts.size} QUEUED",
                            color = TextCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            items(uiState.scheduledPosts, key = { it.id }) { post ->
                HolographicGlassCard(
                    borderColor = if (post.status == PostStatus.PUBLISHED) Color(0xFF00E676) else if (post.status == PostStatus.FAILED) CrimsonWeb else GlassBorder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Thumbnail Preview
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x33101E36))
                                .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = post.mediaUrl,
                                contentDescription = "Media Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xCC000000),
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(2.dp)
                            ) {
                                Text(
                                    text = post.mediaType.name,
                                    color = CyanNeon,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Details
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = post.scheduledTimeFormatted,
                                    color = CyanNeon,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (post.status) {
                                        PostStatus.PUBLISHED -> Color(0x3300E676)
                                        PostStatus.FAILED -> CrimsonGlow
                                        PostStatus.PUBLISHING -> Color(0x3300F0FF)
                                        PostStatus.QUEUED -> GlassSurfaceBright
                                    }
                                ) {
                                    Text(
                                        text = post.status.name,
                                        color = when (post.status) {
                                            PostStatus.PUBLISHED -> Color(0xFF00E676)
                                            PostStatus.FAILED -> CrimsonWeb
                                            PostStatus.PUBLISHING -> CyanNeon
                                            PostStatus.QUEUED -> TextMuted
                                        },
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = post.caption,
                                color = TextWhite,
                                fontSize = 11.sp,
                                maxLines = 2,
                                lineHeight = 15.sp
                            )

                            if (post.errorMessage != null) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = post.errorMessage,
                                    color = if (post.status == PostStatus.PUBLISHED) Color(0xFF00E676) else CrimsonWeb,
                                    fontSize = 9.sp,
                                    maxLines = 1,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (post.status != PostStatus.PUBLISHED) {
                                    Button(
                                        onClick = { onPublishInstagramNow(post.id) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0x3300E676),
                                            contentColor = Color(0xFF00E676)
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text("Publish Now", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                IconButton(
                                    onClick = { onCancelScheduledPost(post.id) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Cancel Post",
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. MT5 TRADING INTEGRATION TAB
        // ==========================================
        if (selectedTab == AutomationViewTab.MT5_TRADING) {
            val mt5 = uiState.mt5State
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
                                Surface(
                                    shape = CircleShape,
                                    color = CyanGlow,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Hub,
                                            contentDescription = "MT5",
                                            tint = CyanNeon,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "METATRADER 5 EA BRIDGE",
                                        color = TextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = mt5.serverStatus,
                                        color = TextCyan,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0x3300E676)
                            ) {
                                Text(
                                    text = "ONLINE",
                                    color = Color(0xFF00E676),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("BALANCE", fontSize = 10.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                                Text("$${String.format("%,.2f", mt5.balance)}", fontSize = 14.sp, color = TextWhite, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Column {
                                Text("EQUITY", fontSize = 10.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                                Text("$${String.format("%,.2f", mt5.equity)}", fontSize = 14.sp, color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Column {
                                Text("POSITIONS", fontSize = 10.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                                Text("${mt5.openPositions} Active", fontSize = 14.sp, color = TextWhite, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Column {
                                Text("MARGIN", fontSize = 10.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                                Text("${mt5.marginLevel}%", fontSize = 14.sp, color = CyanNeon, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onExecuteTrade("BUY", tradeLotSize) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676), contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("mt5_buy_btn")
                            ) {
                                Text("BUY $tradeLotSize LOT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { onExecuteTrade("SELL", tradeLotSize) },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonWeb, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("mt5_sell_btn")
                            ) {
                                Text("SELL $tradeLotSize LOT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. ALL CONFIGURED WORKFLOWS TAB
        // ==========================================
        if (selectedTab == AutomationViewTab.ALL_WORKFLOWS) {
            items(uiState.automations, key = { it.id }) { workflow ->
                HolographicGlassCard(
                    borderColor = if (workflow.isRunning) CyanNeon else CrimsonWeb,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (workflow.isRunning) Color(0xFF00E676) else CrimsonWeb)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = workflow.name,
                                        color = TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = workflow.description,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Switch(
                                checked = workflow.isRunning,
                                onCheckedChange = { onToggleWorkflow(workflow.id) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = CyanNeon,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = Color(0x33202E4A)
                                ),
                                modifier = Modifier.testTag("switch_${workflow.id}")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = workflow.statusText,
                                color = if (workflow.isRunning) TextCyan else CrimsonWeb,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Executions: ${workflow.executionCount}",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Live Automation Logs (Always accessible at bottom)
        item {
            HolographicGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "Logs",
                            tint = CyanNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "REAL-TIME AUTOMATION LOGS",
                            color = CyanNeon,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    uiState.automationLogs.take(5).forEach { log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "[${log.timestamp}]",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${log.source}:",
                                color = if (log.source.contains("INSTAGRAM")) CrimsonWeb else CyanNeon,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = log.message,
                                color = TextWhite,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
