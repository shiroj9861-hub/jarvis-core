package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisCyanPrimary
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary

@Composable
fun MetricGauge(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    unit: String = "",
    fraction: Float, // 0.0f - 1.0f
    color: Color = JarvisCyanPrimary,
    icon: ImageVector? = null
) {
    val animatedFraction by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "gaugeFraction"
    )

    HoloCard(
        modifier = modifier,
        accentGlowColor = color.copy(alpha = 0.3f),
        showCornerBrackets = false
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    color = JarvisTextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                if (icon != null) {
                    androidx.compose.material3.Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.height(16.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    color = JarvisTextPrimary,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = unit,
                        color = color,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            // Progress track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(JarvisSurfaceElevated)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedFraction)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(color.copy(alpha = 0.6f), color)
                            )
                        )
                )
            }
        }
    }
}
