package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NeuralLogEntity
import com.example.ui.theme.JarvisAmberAccent
import com.example.ui.theme.JarvisCodeBackground
import com.example.ui.theme.JarvisCyanPrimary
import com.example.ui.theme.JarvisGreenSuccess
import com.example.ui.theme.JarvisPurpleEvolve
import com.example.ui.theme.JarvisRedAlert
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TerminalConsole(
    modifier: Modifier = Modifier,
    logs: List<NeuralLogEntity>,
    maxHeight: Dp = 220.dp,
    title: String = "JARVIS NEURAL COGNITION STREAM"
) {
    val listState = rememberLazyListState()
    val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(JarvisCodeBackground)
            .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(8.dp))
    ) {
        // Terminal Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0F1D))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(JarvisRedAlert))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(JarvisAmberAccent))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(JarvisGreenSuccess))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = JarvisCyanPrimary,
                letterSpacing = 0.5.sp
            )
        }

        // Terminal Log List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeight)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(logs, key = { it.id }) { log ->
                val levelColor = when (log.level) {
                    "CRITICAL" -> JarvisRedAlert
                    "WARN" -> JarvisAmberAccent
                    "PATCH" -> JarvisGreenSuccess
                    "EVOLVE" -> JarvisPurpleEvolve
                    else -> JarvisCyanPrimary
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = timeFormat.format(Date(log.timestamp)),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = JarvisTextMuted,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = "[${log.subsystem}]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = levelColor,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = log.message,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = JarvisTextPrimary
                    )
                }
            }
        }
    }
}
