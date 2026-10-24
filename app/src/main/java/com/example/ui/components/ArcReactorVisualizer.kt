package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.JarvisAmberAccent
import com.example.ui.theme.JarvisCyanPrimary
import com.example.ui.theme.JarvisGreenSuccess
import com.example.ui.theme.JarvisPurpleEvolve
import com.example.ui.theme.JarvisRedAlert
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

enum class CoreMode {
    NOMINAL,
    FAULT_DETECTED,
    SELF_HEALING,
    EVOLVING,
    OVERDRIVE
}

@Composable
fun ArcReactorVisualizer(
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    mode: CoreMode = CoreMode.NOMINAL,
    energyLevel: Float = 0.98f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ArcReactorTransition")

    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outerRotation"
    )

    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "innerRotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val targetColor = when (mode) {
        CoreMode.NOMINAL -> JarvisCyanPrimary
        CoreMode.FAULT_DETECTED -> JarvisRedAlert
        CoreMode.SELF_HEALING -> JarvisAmberAccent
        CoreMode.EVOLVING -> JarvisPurpleEvolve
        CoreMode.OVERDRIVE -> JarvisGreenSuccess
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(400),
        label = "coreColor"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val maxRadius = min(this.size.width, this.size.height) / 2f - 8.dp.toPx()

            // 1. Holographic ambient glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        animatedColor.copy(alpha = 0.35f * pulseScale),
                        animatedColor.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius * 1.05f
                ),
                radius = maxRadius * 1.05f,
                center = center
            )

            // 2. Outer Ring with tick marks
            drawCircle(
                color = animatedColor.copy(alpha = 0.4f),
                radius = maxRadius,
                center = center,
                style = Stroke(width = 1.8.dp.toPx())
            )

            rotate(outerRotation, pivot = center) {
                val tickCount = 36
                for (i in 0 until tickCount) {
                    val angle = (i * 360f / tickCount) * (PI / 180f)
                    val r1 = maxRadius - if (i % 3 == 0) 9.dp.toPx() else 4.dp.toPx()
                    val r2 = maxRadius
                    val p1 = Offset(
                        center.x + (r1 * cos(angle)).toFloat(),
                        center.y + (r1 * sin(angle)).toFloat()
                    )
                    val p2 = Offset(
                        center.x + (r2 * cos(angle)).toFloat(),
                        center.y + (r2 * sin(angle)).toFloat()
                    )
                    drawLine(
                        color = animatedColor.copy(alpha = if (i % 3 == 0) 0.8f else 0.35f),
                        start = p1,
                        end = p2,
                        strokeWidth = if (i % 3 == 0) 2.dp.toPx() else 1.dp.toPx()
                    )
                }

                // Segmented arcs
                val arcSegments = 4
                for (s in 0 until arcSegments) {
                    val startAngle = s * 90f + 10f
                    drawArc(
                        color = animatedColor.copy(alpha = 0.85f),
                        startAngle = startAngle,
                        sweepAngle = 70f,
                        useCenter = false,
                        topLeft = Offset(center.x - maxRadius * 0.82f, center.y - maxRadius * 0.82f),
                        size = Size(maxRadius * 1.64f, maxRadius * 1.64f),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // 3. Middle Rotating Ring
            val midRadius = maxRadius * 0.65f
            rotate(innerRotation, pivot = center) {
                drawCircle(
                    color = animatedColor.copy(alpha = 0.6f),
                    radius = midRadius,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                val innerArcs = 6
                for (k in 0 until innerArcs) {
                    val startAngle = k * 60f + 8f
                    drawArc(
                        color = animatedColor.copy(alpha = 0.9f),
                        startAngle = startAngle,
                        sweepAngle = 44f,
                        useCenter = false,
                        topLeft = Offset(center.x - midRadius * 0.85f, center.y - midRadius * 0.85f),
                        size = Size(midRadius * 1.7f, midRadius * 1.7f),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // 4. Center Core: Inscribed Arc Triangle and Pulsing Heart
            val coreRadius = maxRadius * 0.38f * pulseScale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.95f),
                        animatedColor.copy(alpha = 0.8f),
                        animatedColor.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = coreRadius
                ),
                radius = coreRadius,
                center = center
            )

            // Inner core geometric triangle
            rotate(outerRotation * 0.5f, pivot = center) {
                drawArcTriangle(center, coreRadius * 0.85f, animatedColor)
            }
        }
    }
}

private fun DrawScope.drawArcTriangle(center: Offset, radius: Float, color: Color) {
    val path = Path()
    for (i in 0..2) {
        val angle = (i * 120f - 90f) * (PI / 180f)
        val px = center.x + (radius * cos(angle)).toFloat()
        val py = center.y + (radius * sin(angle)).toFloat()
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    drawPath(
        path = path,
        color = Color.White.copy(alpha = 0.9f),
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    )
}
