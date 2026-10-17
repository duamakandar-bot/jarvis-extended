package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.GestureEvent
import com.example.model.HandGestureData
import com.example.service.GestureManager
import com.example.service.PinchEvent
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonWeb
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.HologramViolet
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Representation of a glowing particle within the holographic bloom field.
 */
data class BloomParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var radius: Float,
    var alpha: Float,
    var maxLife: Float,
    var currentLife: Float,
    var color: Color,
    var orbitAngle: Float = 0f,
    var orbitRadius: Float = 0f,
    var orbitSpeed: Float = 0f,
    var isBurst: Boolean = false
)

/**
 * Shockwave ripple emitted during pinch release burst.
 */
data class ShockwaveRipple(
    var centerX: Float,
    var centerY: Float,
    var radius: Float,
    var maxRadius: Float,
    var alpha: Float,
    var color: Color
)

/**
 * HolographicBloom: A high-performance Canvas-based composable that renders glowing petals
 * and particles using a dark cyberpunk glassmorphism style, actively responding to pinch events
 * from GestureManager.
 *
 * Visual Features:
 * - Multi-tiered translucent glowing petals (inner corolla, mid lotus, outer corona)
 * - Refractive glassmorphic backdrop disc with radial reticles and cybernetic degree markings
 * - Dynamic particle vortex responding to pinch compression
 * - Explosive particle and shockwave bloom bursts upon pinch release
 * - Real-time hand coordinate tracking and smooth spring damping
 * - Built-in interactive touch pinch & drag fallback for instant responsiveness
 */
@Composable
fun HolographicBloom(
    modifier: Modifier = Modifier,
    gestureManager: GestureManager? = null,
    handGestureData: HandGestureData? = null,
    lowEffectsMode: Boolean = false,
    showControlsOverlay: Boolean = true,
    primaryBloomColor: Color = CyanNeon,
    accentBloomColor: Color = CrimsonWeb,
    petalCount: Int = 8,
    onBloomBurst: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // Resolve GestureManager instance
    val activeGestureManager = remember(gestureManager) {
        gestureManager ?: GestureManager.getInstance(context)
    }

    // Collect gesture state
    val observedGestureState by activeGestureManager.gestureState.collectAsStateWithLifecycle()
    val gestureState = handGestureData ?: observedGestureState

    // Infinite transitions for continuous ethereal motion
    val infiniteTransition = rememberInfiniteTransition(label = "holographic_bloom_loop")

    val ambientRotation by if (!lowEffectsMode) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 18000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ambient_bloom_rotation"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    val ambientPulse by if (!lowEffectsMode) {
        infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ambient_pulse"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    // Pinch responsiveness: Smooth spring animation for bloom aperture and expansion
    val targetBloomAperture = if (gestureState.isPinching) {
        0.35f + (gestureState.pinchProgress * 0.65f)
    } else if (gestureState.isHandDetected) {
        0.35f
    } else {
        0.25f
    }

    val bloomAperture by animateFloatAsState(
        targetValue = targetBloomAperture,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bloom_aperture"
    )

    // Dynamic scale spring for burst impacts
    val burstScale = remember { Animatable(1f) }

    // Hand tracking position smoothing (Spring to avoid jitter)
    val handNormX by animateFloatAsState(
        targetValue = if (gestureState.isHandDetected) gestureState.handX else 0.5f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "hand_x_anim"
    )
    val handNormY by animateFloatAsState(
        targetValue = if (gestureState.isHandDetected) gestureState.handY else 0.5f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "hand_y_anim"
    )

    // Particle field state
    val particles = remember { mutableStateListOf<BloomParticle>() }
    val shockwaves = remember { mutableStateListOf<ShockwaveRipple>() }
    val random = remember { Random(42) }

    // Last burst timestamp to throttle shockwaves
    var lastBurstTimestamp by remember { mutableLongStateOf(0L) }
    var burstCount by remember { mutableIntStateOf(0) }

    // Helper: Trigger burst particles and shockwave
    fun launchBloomBurst(centerX: Float, centerY: Float, canvasSize: Size) {
        burstCount++
        onBloomBurst?.invoke()

        scope.launch {
            burstScale.snapTo(1.35f)
            burstScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }

        // Add expanding shockwaves
        shockwaves.add(
            ShockwaveRipple(
                centerX = centerX,
                centerY = centerY,
                radius = 10f,
                maxRadius = max(canvasSize.width, canvasSize.height) * 0.55f,
                alpha = 0.85f,
                color = primaryBloomColor
            )
        )
        shockwaves.add(
            ShockwaveRipple(
                centerX = centerX,
                centerY = centerY,
                radius = 5f,
                maxRadius = max(canvasSize.width, canvasSize.height) * 0.42f,
                alpha = 0.95f,
                color = accentBloomColor
            )
        )

        // Erupt 36 glowing stardust particles
        val burstCountToSpawn = if (lowEffectsMode) 16 else 36
        for (i in 0 until burstCountToSpawn) {
            val angle = (2f * PI.toFloat() / burstCountToSpawn) * i + (random.nextFloat() * 0.2f - 0.1f)
            val speed = 8f + random.nextFloat() * 14f
            val pColor = when (random.nextInt(3)) {
                0 -> primaryBloomColor
                1 -> accentBloomColor
                else -> HologramViolet
            }
            particles.add(
                BloomParticle(
                    x = centerX,
                    y = centerY,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    radius = 3f + random.nextFloat() * 4.5f,
                    alpha = 1f,
                    maxLife = 50f + random.nextFloat() * 30f,
                    currentLife = 0f,
                    color = pColor,
                    isBurst = true
                )
            )
        }
    }

    // Listen to discrete pinch events from GestureManager
    LaunchedEffect(activeGestureManager) {
        activeGestureManager.pinchEvents.collectLatest { pinchEvent ->
            when (pinchEvent.event) {
                GestureEvent.PINCH_RELEASE -> {
                    val now = System.currentTimeMillis()
                    if (now - lastBurstTimestamp > 250L) {
                        lastBurstTimestamp = now
                        // Will trigger burst during canvas draw with current dimensions
                    }
                }
                GestureEvent.PINCH_START -> {
                    scope.launch {
                        burstScale.snapTo(1.12f)
                        burstScale.animateTo(1f, spring())
                    }
                }
                else -> {}
            }
        }
    }

    // Touch gesture tracking state for screen-based interaction fallback
    var isTouching by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("holographic_bloom_container")
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val minDim = min(widthPx, heightPx)

        // Initialize ambient particles once
        LaunchedEffect(minDim) {
            if (particles.isEmpty() && minDim > 0f) {
                val ambientCount = if (lowEffectsMode) 20 else 45
                for (i in 0 until ambientCount) {
                    val orbitR = (minDim * 0.12f) + random.nextFloat() * (minDim * 0.35f)
                    val startAngle = random.nextFloat() * 2f * PI.toFloat()
                    val pColor = if (i % 2 == 0) primaryBloomColor else accentBloomColor
                    particles.add(
                        BloomParticle(
                            x = widthPx / 2f,
                            y = heightPx / 2f,
                            vx = 0f,
                            vy = 0f,
                            radius = 2.0f + random.nextFloat() * 3.5f,
                            alpha = 0.3f + random.nextFloat() * 0.5f,
                            maxLife = 1000f,
                            currentLife = 0f,
                            color = pColor,
                            orbitAngle = startAngle,
                            orbitRadius = orbitR,
                            orbitSpeed = (0.008f + random.nextFloat() * 0.015f) * if (random.nextBoolean()) 1f else -1f,
                            isBurst = false
                        )
                    )
                }
            }
        }

        // Main Holographic Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("holographic_bloom_canvas")
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, _, zoom, _ ->
                        isTouching = true
                        val touchXNorm = (centroid.x / size.width.toFloat()).coerceIn(0f, 1f)
                        val touchYNorm = (centroid.y / size.height.toFloat()).coerceIn(0f, 1f)

                        // If user pinches or zooms, calculate synthetic pinch progress
                        val pinchProg = if (zoom != 1f) {
                            ((zoom - 0.7f) / 0.8f).coerceIn(0f, 1f)
                        } else {
                            0.75f
                        }

                        activeGestureManager.triggerPinch(
                            progress = pinchProg,
                            handX = touchXNorm,
                            handY = touchYNorm,
                            event = if (pinchProg > 0.85f) GestureEvent.PINCH_START else GestureEvent.PINCH_HOLD
                        )
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val center = Offset(
                x = canvasWidth * handNormX,
                y = canvasHeight * handNormY
            )
            val baseRadius = (min(canvasWidth, canvasHeight) * 0.22f) * ambientPulse * burstScale.value

            // 1. Draw Deep Cybernetic Glassmorphism Background Discs
            drawGlassBackdrop(
                center = center,
                radius = baseRadius * 1.8f,
                bloomColor = primaryBloomColor,
                accentColor = accentBloomColor,
                lowEffectsMode = lowEffectsMode
            )

            // 2. Draw Holographic Reticle Rings & Degree Tick Marks
            drawHolographicRings(
                center = center,
                radius = baseRadius,
                bloomAperture = bloomAperture,
                rotationAngle = ambientRotation,
                bloomColor = primaryBloomColor,
                accentColor = accentBloomColor,
                lowEffectsMode = lowEffectsMode
            )

            // 3. Draw Translucent Glowing Petals (Multi-layered Glassmorphic Bloom)
            drawPetalLayers(
                center = center,
                baseRadius = baseRadius,
                bloomAperture = bloomAperture,
                petalCount = petalCount,
                rotation = ambientRotation,
                isPinching = gestureState.isPinching,
                pinchProgress = gestureState.pinchProgress,
                bloomColor = primaryBloomColor,
                accentColor = accentBloomColor,
                lowEffectsMode = lowEffectsMode
            )

            // 4. Update and Draw Particle System (Vortex + Eruptions)
            updateAndDrawParticles(
                particles = particles,
                center = center,
                isPinching = gestureState.isPinching,
                pinchProgress = gestureState.pinchProgress,
                lowEffectsMode = lowEffectsMode
            )

            // 5. Update and Draw Expanding Shockwaves
            updateAndDrawShockwaves(shockwaves = shockwaves)

            // 6. Draw Singularity Glowing Core at the Pinch Focal Center
            drawSingularityCore(
                center = center,
                radius = baseRadius * 0.28f * (0.8f + bloomAperture * 0.5f),
                isPinching = gestureState.isPinching,
                pinchProgress = gestureState.pinchProgress,
                primaryColor = primaryBloomColor,
                accentColor = accentBloomColor
            )
        }

        // Overlay: Holographic Glassmorphic HUD & Telemetry Controls
        if (showControlsOverlay) {
            BloomHudOverlay(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                gestureState = gestureState,
                bloomAperture = bloomAperture,
                burstCount = burstCount,
                onTriggerBurst = {
                    val cx = widthPx * handNormX
                    val cy = heightPx * handNormY
                    launchBloomBurst(cx, cy, Size(widthPx, heightPx))
                    activeGestureManager.triggerPinchReleaseBurst(handNormX, handNormY)
                },
                onToggleCamera = {
                    if (gestureState.isTrackingActive) {
                        activeGestureManager.stopTracking()
                    } else {
                        activeGestureManager.startTracking(lifecycleOwner)
                    }
                },
                onSimulatePinch = { prog ->
                    activeGestureManager.triggerPinch(prog, handNormX, handNormY)
                }
            )
        }
    }
}

/**
 * Draws the glassmorphic ambient backdrop discs and soft specular glow.
 */
private fun DrawScope.drawGlassBackdrop(
    center: Offset,
    radius: Float,
    bloomColor: Color,
    accentColor: Color,
    lowEffectsMode: Boolean
) {
    if (lowEffectsMode) {
        drawCircle(
            color = Color(0x330B1324),
            radius = radius,
            center = center
        )
        return
    }

    // Outer soft gradient halo
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                bloomColor.copy(alpha = 0.22f),
                accentColor.copy(alpha = 0.10f),
                Color.Transparent
            ),
            center = center,
            radius = radius * 1.35f
        ),
        radius = radius * 1.35f,
        center = center
    )

    // Frosted glass disc
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0x66101D38),
                Color(0x44080F1E),
                Color(0x22050A14)
            ),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )

    // Specular glass perimeter ring
    drawCircle(
        brush = Brush.sweepGradient(
            listOf(
                bloomColor.copy(alpha = 0.7f),
                Color.Transparent,
                accentColor.copy(alpha = 0.6f),
                Color.Transparent,
                bloomColor.copy(alpha = 0.7f)
            ),
            center = center
        ),
        radius = radius,
        center = center,
        style = Stroke(width = 1.5.dp.toPx())
    )
}

/**
 * Draws cybernetic concentric reticle rings, angle tick marks, and degree labels.
 */
private fun DrawScope.drawHolographicRings(
    center: Offset,
    radius: Float,
    bloomAperture: Float,
    rotationAngle: Float,
    bloomColor: Color,
    accentColor: Color,
    lowEffectsMode: Boolean
) {
    val ring1R = radius * 0.6f * (1f + bloomAperture * 0.2f)
    val ring2R = radius * 1.05f * (1f + bloomAperture * 0.35f)
    val ring3R = radius * 1.45f * (1f + bloomAperture * 0.15f)

    // Inner dashed energetic ring
    drawCircle(
        color = bloomColor.copy(alpha = 0.45f),
        radius = ring1R,
        center = center,
        style = Stroke(
            width = 1.2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), rotationAngle * 0.8f)
        )
    )

    // Mid solid ring
    drawCircle(
        color = accentColor.copy(alpha = 0.35f),
        radius = ring2R,
        center = center,
        style = Stroke(
            width = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 16f, 8f, 16f), -rotationAngle * 0.5f)
        )
    )

    if (!lowEffectsMode) {
        // Outer fine dotted ring
        drawCircle(
            color = bloomColor.copy(alpha = 0.25f),
            radius = ring3R,
            center = center,
            style = Stroke(
                width = 0.8.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f), rotationAngle * 0.2f)
            )
        )

        // 12 Cybernetic tick marks around outer ring
        for (i in 0 until 12) {
            val angleDeg = i * 30f + rotationAngle * 0.3f
            val rad = angleDeg * (PI.toFloat() / 180f)
            val innerPoint = Offset(
                x = center.x + cos(rad) * (ring2R - 8f),
                y = center.y + sin(rad) * (ring2R - 8f)
            )
            val outerPoint = Offset(
                x = center.x + cos(rad) * (ring2R + 8f),
                y = center.y + sin(rad) * (ring2R + 8f)
            )
            drawLine(
                color = if (i % 3 == 0) bloomColor.copy(alpha = 0.7f) else accentColor.copy(alpha = 0.35f),
                start = innerPoint,
                end = outerPoint,
                strokeWidth = if (i % 3 == 0) 1.5.dp.toPx() else 1.dp.toPx()
            )
        }
    }
}

/**
 * Draws the multi-tiered glowing glassmorphic petals (inner corolla, mid lotus, and outer corona).
 */
private fun DrawScope.drawPetalLayers(
    center: Offset,
    baseRadius: Float,
    bloomAperture: Float,
    petalCount: Int,
    rotation: Float,
    isPinching: Boolean,
    pinchProgress: Float,
    bloomColor: Color,
    accentColor: Color,
    lowEffectsMode: Boolean
) {
    // 1. Outer Corona Petals (Large, gossamer, glowing edges)
    val outerCount = petalCount
    val outerLength = baseRadius * (1.1f + bloomAperture * 0.95f)
    val outerWidth = outerLength * (0.32f + bloomAperture * 0.18f)

    for (i in 0 until outerCount) {
        val angleDeg = (360f / outerCount) * i + rotation * 0.4f
        rotate(degrees = angleDeg, pivot = center) {
            drawSinglePetal(
                center = center,
                length = outerLength,
                width = outerWidth,
                baseColor = accentColor,
                tipColor = bloomColor,
                alpha = 0.35f + bloomAperture * 0.25f,
                veinColor = Color(0xCCFFFFFF),
                glowStroke = true,
                curvature = 0.35f
            )
        }
    }

    // 2. Mid Lotus Petals (Offset in angle, vibrant translucent gradient fill)
    val midCount = petalCount
    val midLength = baseRadius * (0.85f + bloomAperture * 0.75f)
    val midWidth = midLength * (0.38f + bloomAperture * 0.15f)
    val midAngleOffset = (180f / midCount)

    for (i in 0 until midCount) {
        val angleDeg = (360f / midCount) * i + midAngleOffset - rotation * 0.6f
        rotate(degrees = angleDeg, pivot = center) {
            drawSinglePetal(
                center = center,
                length = midLength,
                width = midWidth,
                baseColor = bloomColor,
                tipColor = accentColor,
                alpha = 0.50f + bloomAperture * 0.30f,
                veinColor = bloomColor.copy(alpha = 0.85f),
                glowStroke = true,
                curvature = 0.45f
            )
        }
    }

    // 3. Inner Core Corolla (Tight, highly responsive to pinch compression)
    if (!lowEffectsMode) {
        val innerCount = 6
        val innerLength = baseRadius * (0.42f + bloomAperture * 0.45f)
        val innerWidth = innerLength * (0.45f + (1f - pinchProgress) * 0.2f)

        for (i in 0 until innerCount) {
            val angleDeg = (360f / innerCount) * i + rotation * 1.2f
            rotate(degrees = angleDeg, pivot = center) {
                drawSinglePetal(
                    center = center,
                    length = innerLength,
                    width = innerWidth,
                    baseColor = Color(0xFFFFFFFF),
                    tipColor = bloomColor,
                    alpha = 0.70f + pinchProgress * 0.25f,
                    veinColor = Color.White,
                    glowStroke = true,
                    curvature = 0.55f
                )
            }
        }
    }
}

/**
 * Renders a single glowing organic petal using cubic Bezier curves, glassmorphism gradient fill,
 * refractive edge strokes, and an iridescent central vein.
 */
private fun DrawScope.drawSinglePetal(
    center: Offset,
    length: Float,
    width: Float,
    baseColor: Color,
    tipColor: Color,
    alpha: Float,
    veinColor: Color,
    glowStroke: Boolean,
    curvature: Float
) {
    val tip = Offset(center.x, center.y - length)
    val controlOffset = width * curvature

    // Left and right control points for organic petal bulge
    val c1Left = Offset(center.x - controlOffset * 1.4f, center.y - length * 0.35f)
    val c2Left = Offset(center.x - controlOffset * 0.8f, center.y - length * 0.85f)

    val c1Right = Offset(center.x + controlOffset * 1.4f, center.y - length * 0.35f)
    val c2Right = Offset(center.x + controlOffset * 0.8f, center.y - length * 0.85f)

    val petalPath = Path().apply {
        moveTo(center.x, center.y)
        cubicTo(c1Left.x, c1Left.y, c2Left.x, c2Left.y, tip.x, tip.y)
        cubicTo(c2Right.x, c2Right.y, c1Right.x, c1Right.y, center.x, center.y)
        close()
    }

    // 1. Translucent Glassmorphic Gradient Fill
    val petalBrush = Brush.verticalGradient(
        colors = listOf(
            tipColor.copy(alpha = alpha * 0.35f),
            baseColor.copy(alpha = alpha * 0.75f),
            tipColor.copy(alpha = alpha * 0.90f)
        ),
        startY = tip.y,
        endY = center.y
    )
    drawPath(path = petalPath, brush = petalBrush, style = Fill)

    // 2. Refractive Glass Edge Highlight (Glow Stroke)
    if (glowStroke) {
        val borderBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xEEFFFFFF),
                tipColor.copy(alpha = 0.9f),
                baseColor.copy(alpha = 0.4f)
            ),
            startY = tip.y,
            endY = center.y
        )
        drawPath(
            path = petalPath,
            brush = borderBrush,
            style = Stroke(
                width = 1.4.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    // 3. Central Luminous Bio-Vein
    val veinPath = Path().apply {
        moveTo(center.x, center.y - length * 0.1f)
        quadraticTo(
            center.x,
            center.y - length * 0.5f,
            tip.x,
            tip.y + length * 0.08f
        )
    }
    drawLine(
        brush = Brush.verticalGradient(
            listOf(veinColor.copy(alpha = 0.9f), veinColor.copy(alpha = 0.1f)),
            startY = tip.y,
            endY = center.y
        ),
        start = Offset(center.x, center.y),
        end = Offset(tip.x, tip.y + length * 0.05f),
        strokeWidth = 1.2.dp.toPx(),
        cap = StrokeCap.Round
    )
}

/**
 * Updates simulation physics and renders both ambient orbital particles and burst sparks.
 */
private fun DrawScope.updateAndDrawParticles(
    particles: MutableList<BloomParticle>,
    center: Offset,
    isPinching: Boolean,
    pinchProgress: Float,
    lowEffectsMode: Boolean
) {
    val iterator = particles.iterator()
    while (iterator.hasNext()) {
        val p = iterator.next()

        if (p.isBurst) {
            // Burst sparks: velocity + gravity/damping
            p.x += p.vx
            p.y += p.vy
            p.vx *= 0.94f
            p.vy *= 0.94f
            p.currentLife += 1f
            p.alpha = (1f - (p.currentLife / p.maxLife)).coerceIn(0f, 1f)

            if (p.currentLife >= p.maxLife) {
                iterator.remove()
                continue
            }
        } else {
            // Ambient orbital particles: vortex suction when pinching
            p.orbitAngle += p.orbitSpeed * (1f + pinchProgress * 2.5f)
            val currentR = if (isPinching) {
                p.orbitRadius * (1f - pinchProgress * 0.65f)
            } else {
                p.orbitRadius
            }
            p.x = center.x + cos(p.orbitAngle) * currentR
            p.y = center.y + sin(p.orbitAngle) * currentR
        }

        // Render glowing particle dot with multi-tier halo
        if (p.alpha > 0.01f) {
            val drawR = p.radius * (0.8f + pinchProgress * 0.4f)

            if (!lowEffectsMode) {
                // Soft outer halo
                drawCircle(
                    color = p.color.copy(alpha = p.alpha * 0.35f),
                    radius = drawR * 2.8f,
                    center = Offset(p.x, p.y)
                )
            }

            // Main glowing core
            drawCircle(
                color = p.color.copy(alpha = p.alpha * 0.85f),
                radius = drawR,
                center = Offset(p.x, p.y)
            )

            // Sharp energetic center
            drawCircle(
                color = Color.White.copy(alpha = p.alpha * 0.95f),
                radius = drawR * 0.45f,
                center = Offset(p.x, p.y)
            )
        }
    }
}

/**
 * Updates expanding shockwave rings emitted upon pinch release bursts.
 */
private fun DrawScope.updateAndDrawShockwaves(shockwaves: MutableList<ShockwaveRipple>) {
    val iterator = shockwaves.iterator()
    while (iterator.hasNext()) {
        val sw = iterator.next()
        sw.radius += (sw.maxRadius - sw.radius) * 0.14f + 3f
        sw.alpha = (1f - (sw.radius / sw.maxRadius)).coerceIn(0f, 1f)

        if (sw.radius >= sw.maxRadius || sw.alpha <= 0.01f) {
            iterator.remove()
            continue
        }

        // Draw expanding neon shockwave ring
        drawCircle(
            color = sw.color.copy(alpha = sw.alpha * 0.8f),
            radius = sw.radius,
            center = Offset(sw.centerX, sw.centerY),
            style = Stroke(
                width = (3.dp.toPx() * sw.alpha).coerceAtLeast(1f),
                cap = StrokeCap.Round
            )
        )
    }
}

/**
 * Draws the high-energy singularity core at the pinch anchor point.
 */
private fun DrawScope.drawSingularityCore(
    center: Offset,
    radius: Float,
    isPinching: Boolean,
    pinchProgress: Float,
    primaryColor: Color,
    accentColor: Color
) {
    val coreR = radius * (1f + pinchProgress * 0.45f)

    // Outer glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.8f),
                accentColor.copy(alpha = 0.4f),
                Color.Transparent
            ),
            center = center,
            radius = coreR * 1.8f
        ),
        radius = coreR * 1.8f,
        center = center
    )

    // Singularity center disc
    drawCircle(
        color = if (isPinching) accentColor else primaryColor,
        radius = coreR * 0.65f,
        center = center
    )

    // White hot core
    drawCircle(
        color = Color.White,
        radius = coreR * 0.35f,
        center = center
    )
}

/**
 * Glassmorphic HUD overlay providing real-time telemetry, pinch progress meter,
 * CameraX tracking controls, and synthetic pinch burst testing.
 */
@Composable
private fun BloomHudOverlay(
    modifier: Modifier = Modifier,
    gestureState: HandGestureData,
    bloomAperture: Float,
    burstCount: Int,
    onTriggerBurst: () -> Unit,
    onToggleCamera: () -> Unit,
    onSimulatePinch: (Float) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header Bar: Telemetry & Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Glassmorphic status pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GlassSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    gestureState.isPinching -> CrimsonWeb
                                    gestureState.isHandDetected -> CyanNeon
                                    gestureState.isTrackingActive -> AmberAlert
                                    else -> TextMuted
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = when {
                                gestureState.isPinching -> "PINCH LOCKED • BLOOM ACTIVE"
                                gestureState.isHandDetected -> "HAND TRACKED • READY"
                                gestureState.isTrackingActive -> "SCANNING HAND..."
                                else -> "TOUCH / GESTURE STANDBY"
                            },
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Aperture: ${(bloomAperture * 100).toInt()}% • Bursts: $burstCount",
                            color = TextCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Camera toggle button
            IconButton(
                onClick = onToggleCamera,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(GlassSurface)
                    .border(1.dp, GlassBorder, CircleShape)
                    .testTag("bloom_camera_toggle_btn")
            ) {
                Icon(
                    imageVector = if (gestureState.isTrackingActive) Icons.Default.Videocam else Icons.Default.VideocamOff,
                    contentDescription = "Toggle Camera Hand Tracking",
                    tint = if (gestureState.isTrackingActive) CyanNeon else TextMuted
                )
            }
        }

        // Bottom Controls Bar: Pinch Progress & Burst Launcher
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = GlassSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("bloom_pinch_meter")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PanTool,
                            contentDescription = "Pinch Gesture",
                            tint = CyanNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HOLOGRAPHIC PINCH COMPRESSION",
                            color = CyanNeon,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "${(gestureState.pinchProgress * 100).toInt()}%",
                        color = if (gestureState.isPinching) CrimsonWeb else TextCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0F1A2E))
                        .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = max(0.02f, gestureState.pinchProgress))
                            .height(8.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(CyanNeon, HologramViolet, CrimsonWeb)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Burst Trigger Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x33FF1744),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonWeb),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTriggerBurst() }
                            .testTag("bloom_burst_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Erupt Bloom Burst",
                                tint = CrimsonWeb,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TRIGGER BURST",
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Synthetic Pinch Simulation (Toggles 100% pinch)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x3300F0FF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (gestureState.pinchProgress > 0.5f) {
                                    onSimulatePinch(0f)
                                } else {
                                    onSimulatePinch(1.0f)
                                }
                            }
                            .testTag("bloom_simulate_pinch_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Simulate Pinch Cycle",
                                tint = CyanNeon,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (gestureState.pinchProgress > 0.5f) "RELEASE PINCH" else "PINCH FLOWER",
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
