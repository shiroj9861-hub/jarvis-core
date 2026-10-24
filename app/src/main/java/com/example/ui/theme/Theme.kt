package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val JarvisColorScheme = darkColorScheme(
    primary = JarvisCyanPrimary,
    onPrimary = JarvisBackground,
    primaryContainer = JarvisSurfaceElevated,
    onPrimaryContainer = JarvisCyanSubtle,
    secondary = JarvisBlueSecondary,
    onSecondary = JarvisBackground,
    secondaryContainer = JarvisSurfaceElevated,
    onSecondaryContainer = JarvisTextSecondary,
    tertiary = JarvisAmberAccent,
    onTertiary = JarvisBackground,
    background = JarvisBackground,
    onBackground = JarvisTextPrimary,
    surface = JarvisSurface,
    onSurface = JarvisTextPrimary,
    surfaceVariant = JarvisSurfaceElevated,
    onSurfaceVariant = JarvisTextSecondary,
    outline = JarvisSurfaceBorder,
    error = JarvisRedAlert,
    onError = JarvisBackground
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = JarvisBackground.toArgb()
                window.navigationBarColor = JarvisBackground.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = JarvisColorScheme,
        typography = Typography,
        content = content
    )
}
