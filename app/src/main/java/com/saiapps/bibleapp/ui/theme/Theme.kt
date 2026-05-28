package com.saiapps.bibleapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Warm paper / ink palette evokes the look of a printed Bible.
private val Paper       = Color(0xFFFBF5EA)
private val PaperSoft   = Color(0xFFF2E9D6)
private val PaperEdge   = Color(0xFFE7DBC1)
private val Ink         = Color(0xFF1F1A12)
private val InkSoft     = Color(0xFF55493A)
private val Gold        = Color(0xFFB8893F)
private val GoldDeep    = Color(0xFF8A6322)
private val Crimson     = Color(0xFFA13E2C)

private val Night        = Color(0xFF15110B)
private val NightSurface = Color(0xFF1F1A12)
private val NightEdge    = Color(0xFF2A2317)
private val NightInk     = Color(0xFFEDE3CE)
private val NightInkSoft = Color(0xFFB9AB8D)
private val GoldGlow     = Color(0xFFE6B772)

private val LightColors = lightColorScheme(
    primary = GoldDeep,
    onPrimary = Color.White,
    primaryContainer = PaperSoft,
    onPrimaryContainer = Ink,
    secondary = Crimson,
    onSecondary = Color.White,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = PaperSoft,
    onSurfaceVariant = InkSoft,
    outline = PaperEdge,
    outlineVariant = PaperEdge,
)

private val DarkColors = darkColorScheme(
    primary = GoldGlow,
    onPrimary = Color.Black,
    primaryContainer = NightEdge,
    onPrimaryContainer = NightInk,
    secondary = Crimson,
    onSecondary = Color.White,
    background = Night,
    onBackground = NightInk,
    surface = NightSurface,
    onSurface = NightInk,
    surfaceVariant = NightEdge,
    onSurfaceVariant = NightInkSoft,
    outline = NightEdge,
    outlineVariant = NightEdge,
)

@Composable
fun BibleTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = BibleTypography,
        content = content
    )
}
