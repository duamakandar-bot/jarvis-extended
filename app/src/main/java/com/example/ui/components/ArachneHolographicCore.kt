package com.example.ui.components

import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CrimsonWeb
import com.example.ui.theme.HologramViolet
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val AGSL_CORE_SHADER = """
    uniform float2 uResolution;
    uniform float uTime;
    uniform float uIntensity;
    uniform float uSpeaking;

    half4 main(in float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * uResolution) / min(uResolution.x, uResolution.y);
        float dist = length(uv);
        float angle = atan(uv.y, uv.x);

        // Interference waves
        float wave1 = sin(dist * 22.0 - uTime * 4.0 + angle * 4.0);
        float wave2 = cos(dist * 30.0 + uTime * 2.5 - angle * 2.0);
        float corePulse = sin(uTime * 3.5) * 0.12 + 0.88;

        // Crimson and Cyan blend calculation
        float blend = 0.5 + 0.5 * sin(angle * 3.0 + uTime * 1.5 + wave1 * 0.4);
        half3 cyan = half3(0.0, 0.941, 1.0);
        half3 crimson = half3(1.0, 0.090, 0.267);
        half3 violet = half3(0.616, 0.306, 0.867);

        half3 color = mix(cyan, crimson, blend);
        color = mix(color, violet, 0.3 * wave2);

        // Bright energetic singularity center
        if (dist < 0.22 * corePulse) {
            float inner = (0.22 * corePulse - dist) / (0.22 * corePulse);
            color = mix(color, half3(0.9, 0.98, 1.0), inner * 0.95);
        }

        // Concentric holographic ripples
        float ring = abs(sin(dist * 40.0 - uTime * 6.0));
        color += cyan * (1.0 - smoothstep(0.0, 0.12, ring)) * 0.35;

        // Horizontal scanlines
        float scanline = sin(fragCoord.y * 1.8 + uTime * 8.0) * 0.08;
        color += scanline;

        // Alpha vignette falloff
        float alpha = smoothstep(0.49, 0.18, dist);
        if (dist > 0.49) alpha = 0.0;

        return half4(color * (1.0 + uSpeaking * 0.45), alpha);
    }
"""

@Composable
fun ArachneHolographicCore(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    isSpeaking: Boolean = false,
    isThinking: Boolean = false,
    isListening: Boolean = false,
    lowEffectsMode: Boolean = false,
    onClick: () -> Unit = {}
) {
    // Pulse animation timer
    val transition = rememberInfiniteTransition(label = "holographic_core_pulse")

    val pulsePhase by if (!lowEffectsMode) {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 2f * PI.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = if (isSpeaking) 1800 else if (isThinking) 2400 else 4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "pulse_phase"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    val rotationAngle by if (!lowEffectsMode) {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = if (isSpeaking) 3500 else if (isThinking) 5000 else 9000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "core_rotation"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    // Touch ripple shockwave
    var shockwaveRadius by remember { mutableFloatStateOf(0f) }

    // AGSL RuntimeShader instance if on API 33+
    val runtimeShader = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                RuntimeShader(AGSL_CORE_SHADER)
            } catch (_: Throwable) {
                null
            }
        } else {
            null
        }
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(size)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .testTag("arachne_holographic_core"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val center = Offset(canvasWidth / 2f, canvasHeight / 2f)
            val maxRadius = minOf(canvasWidth, canvasHeight) / 2f

            val speakingBoost = if (isSpeaking) 1.25f else 1.0f
            val thinkingCrimsonBoost = if (isThinking) 1.5f else 1.0f

            // 1. If AGSL RuntimeShader is available and supported on device, use it as base shader
            if (runtimeShader != null && !lowEffectsMode) {
                try {
                    runtimeShader.setFloatUniform("uResolution", canvasWidth, canvasHeight)
                    runtimeShader.setFloatUniform("uTime", pulsePhase)
                    runtimeShader.setFloatUniform("uIntensity", if (isThinking) 1.5f else 1.0f)
                    runtimeShader.setFloatUniform("uSpeaking", if (isSpeaking) 1.0f else 0.0f)

                    drawCircle(
                        brush = ShaderBrush(runtimeShader),
                        radius = maxRadius * 0.95f,
                        center = center
                    )
                } catch (_: Throwable) {
                    drawFallbackCanvasCore(
                        center = center,
                        maxRadius = maxRadius,
                        pulsePhase = pulsePhase,
                        rotationAngle = rotationAngle,
                        isSpeaking = isSpeaking,
                        isThinking = isThinking,
                        isListening = isListening,
                        lowEffectsMode = lowEffectsMode
                    )
                }
            } else {
                // Universal high-performance multi-layered canvas shader
                drawFallbackCanvasCore(
                    center = center,
                    maxRadius = maxRadius,
                    pulsePhase = pulsePhase,
                    rotationAngle = rotationAngle,
                    isSpeaking = isSpeaking,
                    isThinking = isThinking,
                    isListening = isListening,
                    lowEffectsMode = lowEffectsMode
                )
            }

            // 2. Holographic Spider-Woman Hexagonal Neural Lattice Rings
            drawSpiderNeuralLattice(
                center = center,
                maxRadius = maxRadius,
                pulsePhase = pulsePhase,
                rotationAngle = rotationAngle,
                isThinking = isThinking,
                lowEffectsMode = lowEffectsMode
            )

            // 3. Audio Reactivity Wavefronts (when speaking or listening)
            if (isSpeaking || isListening) {
                drawAudioReactionRings(
                    center = center,
                    maxRadius = maxRadius,
                    pulsePhase = pulsePhase,
                    isSpeaking = isSpeaking
                )
            }
        }
    }
}

/**
 * Universal multi-layered Canvas shader effect rendering concentric pulsing gradients,
 * chromatic sweep interference, and central energy singularity.
 */
private fun DrawScope.drawFallbackCanvasCore(
    center: Offset,
    maxRadius: Float,
    pulsePhase: Float,
    rotationAngle: Float,
    isSpeaking: Boolean,
    isThinking: Boolean,
    isListening: Boolean,
    lowEffectsMode: Boolean
) {
    val pulseNorm = (sin(pulsePhase) + 1f) / 2f // 0f .. 1f
    val coreRadius = maxRadius * (0.28f + pulseNorm * 0.08f * (if (isSpeaking) 1.6f else 1.0f))

    // Layer A: Outer Pulsing Ambient Field (Crimson + Cyan)
    val outerGlowBrush = Brush.radialGradient(
        colors = listOf(
            if (isThinking) CrimsonWeb.copy(alpha = 0.45f) else CyanNeon.copy(alpha = 0.45f),
            if (isThinking) CrimsonWeb.copy(alpha = 0.25f) else CrimsonWeb.copy(alpha = 0.25f),
            HologramViolet.copy(alpha = 0.12f),
            Color.Transparent
        ),
        center = center,
        radius = maxRadius * (0.85f + pulseNorm * 0.15f)
    )
    drawCircle(brush = outerGlowBrush, radius = maxRadius, center = center)

    // Layer B: Chromatic Rotating Sweep Gradient (Interference Ring)
    if (!lowEffectsMode) {
        rotate(degrees = rotationAngle, pivot = center) {
            val sweepBrush1 = Brush.sweepGradient(
                colors = listOf(
                    CyanNeon.copy(alpha = 0.7f),
                    CrimsonWeb.copy(alpha = 0.15f),
                    CrimsonWeb.copy(alpha = 0.75f),
                    CyanNeon.copy(alpha = 0.2f),
                    HologramViolet.copy(alpha = 0.6f),
                    CyanNeon.copy(alpha = 0.7f)
                ),
                center = center
            )
            drawCircle(
                brush = sweepBrush1,
                radius = maxRadius * 0.72f,
                center = center,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Counter-rotating sweep ring
        rotate(degrees = -rotationAngle * 1.4f, pivot = center) {
            val sweepBrush2 = Brush.sweepGradient(
                colors = listOf(
                    CrimsonWeb.copy(alpha = 0.6f),
                    Color.Transparent,
                    CyanNeon.copy(alpha = 0.8f),
                    Color.Transparent,
                    CrimsonWeb.copy(alpha = 0.6f)
                ),
                center = center
            )
            drawCircle(
                brush = sweepBrush2,
                radius = maxRadius * 0.52f,
                center = center,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }

    // Layer C: Concentric Wave Ripples
    val ringCount = if (lowEffectsMode) 2 else 4
    for (i in 0 until ringCount) {
        val ringPhase = (pulsePhase + i * (PI.toFloat() / ringCount)) % (2 * PI.toFloat())
        val ringProgress = ringPhase / (2 * PI.toFloat())
        val ringRadius = coreRadius + (maxRadius * 0.9f - coreRadius) * ringProgress
        val ringAlpha = (1f - ringProgress) * 0.65f

        val ringColor = if (i % 2 == 0) {
            if (isThinking) CrimsonWeb.copy(alpha = ringAlpha) else CyanNeon.copy(alpha = ringAlpha)
        } else {
            if (isThinking) CyanNeon.copy(alpha = ringAlpha) else CrimsonWeb.copy(alpha = ringAlpha)
        }

        drawCircle(
            color = ringColor,
            radius = ringRadius,
            center = center,
            style = Stroke(width = (2.2f - ringProgress * 1.2f).dp.toPx())
        )
    }

    // Layer D: White-Hot Cyan AI Core Singularity
    val innerCoreBrush = Brush.radialGradient(
        colors = listOf(
            Color.White,
            Color(0xFFE0FFFF),
            CyanNeon.copy(alpha = 0.95f),
            if (isThinking) CrimsonWeb else CyanNeon.copy(alpha = 0.6f),
            Color.Transparent
        ),
        center = center,
        radius = coreRadius
    )
    drawCircle(brush = innerCoreBrush, radius = coreRadius, center = center)

    // Layer E: Core Iris Flare
    drawCircle(
        color = Color.White,
        radius = coreRadius * 0.38f,
        center = center
    )
}

/**
 * Draws Spider-Woman inspired geometric hexagonal node lattice and radial neural rays
 */
private fun DrawScope.drawSpiderNeuralLattice(
    center: Offset,
    maxRadius: Float,
    pulsePhase: Float,
    rotationAngle: Float,
    isThinking: Boolean,
    lowEffectsMode: Boolean
) {
    val sides = 6
    val latticeRadius = maxRadius * 0.82f

    // Radial web spokes
    val spokeCount = 12
    for (i in 0 until spokeCount) {
        val angleRad = (i * (2 * PI / spokeCount) + Math.toRadians(rotationAngle * 0.25).toFloat()).toFloat()
        val startX = center.x + cos(angleRad) * (maxRadius * 0.28f)
        val startY = center.y + sin(angleRad) * (maxRadius * 0.28f)
        val endX = center.x + cos(angleRad) * latticeRadius
        val endY = center.y + sin(angleRad) * latticeRadius

        val spokeColor = if (i % 2 == 0) {
            CyanNeon.copy(alpha = if (lowEffectsMode) 0.15f else 0.28f)
        } else {
            CrimsonWeb.copy(alpha = if (lowEffectsMode) 0.12f else 0.25f)
        }

        drawLine(
            color = spokeColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 1.dp.toPx()
        )
    }

    // Outer Hexagonal Web Node Rings
    val hexRings = if (lowEffectsMode) listOf(0.48f, 0.82f) else listOf(0.48f, 0.66f, 0.84f)
    for (scale in hexRings) {
        val r = maxRadius * scale
        val path = Path()
        for (i in 0 until sides) {
            val angle = (i * (2 * PI / sides) + Math.toRadians(-rotationAngle * 0.35).toFloat()).toFloat()
            val x = center.x + cos(angle) * r
            val y = center.y + sin(angle) * r
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)

            // Small glowing node dot
            if (!lowEffectsMode) {
                drawCircle(
                    color = if (i % 2 == 0) CyanNeon.copy(alpha = 0.8f) else CrimsonWeb.copy(alpha = 0.8f),
                    radius = 2.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
        path.close()

        drawPath(
            path = path,
            color = if (scale > 0.7f) CrimsonWeb.copy(alpha = 0.32f) else CyanNeon.copy(alpha = 0.35f),
            style = Stroke(width = 1.2.dp.toPx())
        )
    }
}

/**
 * Draws dynamic audio reaction waves simulating voice speech output or mic input
 */
private fun DrawScope.drawAudioReactionRings(
    center: Offset,
    maxRadius: Float,
    pulsePhase: Float,
    isSpeaking: Boolean
) {
    val samplePoints = 48
    val wavePath = Path()
    val baseR = maxRadius * 0.62f

    for (i in 0 until samplePoints) {
        val angle = (i * (2 * PI / samplePoints)).toFloat()
        val freq1 = sin(angle * 7f + pulsePhase * 5f)
        val freq2 = cos(angle * 13f - pulsePhase * 3f)
        val displacement = (freq1 * 6.dp.toPx() + freq2 * 4.dp.toPx()) * (if (isSpeaking) 1.5f else 0.9f)
        val r = baseR + displacement
        val x = center.x + cos(angle) * r
        val y = center.y + sin(angle) * r

        if (i == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
    }
    wavePath.close()

    drawPath(
        path = wavePath,
        color = CyanNeon.copy(alpha = 0.85f),
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    )
}
