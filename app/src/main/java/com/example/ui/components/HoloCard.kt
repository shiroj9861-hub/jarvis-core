package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.JarvisCyanPrimary
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceBorder

@Composable
fun HoloCard(
    modifier: Modifier = Modifier,
    borderColor: Color = JarvisSurfaceBorder,
    accentGlowColor: Color? = null,
    cornerRadius: Dp = 12.dp,
    showCornerBrackets: Boolean = true,
    content: @Composable () -> Unit
) {
    val activeBorder = accentGlowColor ?: borderColor

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color = JarvisSurface.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, activeBorder),
        shadowElevation = if (accentGlowColor != null) 6.dp else 1.dp
    ) {
        Box {
            if (showCornerBrackets) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val bracketLen = 10.dp.toPx()
                    val bracketColor = (accentGlowColor ?: JarvisCyanPrimary).copy(alpha = 0.6f)
                    val strokeW = 1.5.dp.toPx()

                    // Top-Left corner bracket
                    drawLine(bracketColor, Offset(0f, 0f), Offset(bracketLen, 0f), strokeW)
                    drawLine(bracketColor, Offset(0f, 0f), Offset(0f, bracketLen), strokeW)

                    // Top-Right corner bracket
                    drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width - bracketLen, 0f), strokeW)
                    drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width, bracketLen), strokeW)

                    // Bottom-Left corner bracket
                    drawLine(bracketColor, Offset(0f, size.height), Offset(bracketLen, size.height), strokeW)
                    drawLine(bracketColor, Offset(0f, size.height), Offset(0f, size.height - bracketLen), strokeW)

                    // Bottom-Right corner bracket
                    drawLine(bracketColor, Offset(size.width, size.height), Offset(size.width - bracketLen, size.height), strokeW)
                    drawLine(bracketColor, Offset(size.width, size.height), Offset(size.width, size.height - bracketLen), strokeW)
                }
            }

            Box(modifier = Modifier.padding(14.dp)) {
                content()
            }
        }
    }
}
