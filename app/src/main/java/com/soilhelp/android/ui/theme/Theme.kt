package com.soilhelp.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GreenPrimary = Color(0xFF2E7D32)
private val GreenSecondary = Color(0xFF81C784)

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary
)

private val DarkColors = darkColorScheme(
    primary = GreenSecondary,
    secondary = GreenPrimary
)

@Composable
fun SoilHelpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
