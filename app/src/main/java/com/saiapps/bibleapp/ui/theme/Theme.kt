package com.saiapps.bibleapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Paper = Color(0xFFFFFBF7)
private val Ink = Color(0xFF1B1B1F)
private val Accent = Color(0xFF7E5A3C)
private val AccentDark = Color(0xFFD2B48C)
private val NightBg = Color(0xFF14110E)
private val NightInk = Color(0xFFE9E2D6)

private val LightColors = lightColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEFE7DC),
    onSurfaceVariant = Color(0xFF4A4338),
)

private val DarkColors = darkColorScheme(
    primary = AccentDark,
    onPrimary = Color.Black,
    background = NightBg,
    onBackground = NightInk,
    surface = NightBg,
    onSurface = NightInk,
    surfaceVariant = Color(0xFF2A241D),
    onSurfaceVariant = Color(0xFFCFC4B0),
)

@Composable
fun BibleTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
