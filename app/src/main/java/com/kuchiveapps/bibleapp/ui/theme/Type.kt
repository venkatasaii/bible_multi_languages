@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.kuchiveapps.bibleapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kuchiveapps.bibleapp.R

private fun wght(value: Int) = FontVariation.Settings(FontVariation.weight(value))

val LoraFamily = FontFamily(
    Font(R.font.lora, weight = FontWeight.Normal,   variationSettings = wght(400)),
    Font(R.font.lora, weight = FontWeight.Medium,   variationSettings = wght(500)),
    Font(R.font.lora, weight = FontWeight.SemiBold, variationSettings = wght(600)),
    Font(R.font.lora, weight = FontWeight.Bold,     variationSettings = wght(700)),
    Font(R.font.lora_italic, weight = FontWeight.Normal,   style = FontStyle.Italic, variationSettings = wght(400)),
    Font(R.font.lora_italic, weight = FontWeight.SemiBold, style = FontStyle.Italic, variationSettings = wght(600)),
)

val CinzelFamily = FontFamily(
    Font(R.font.cinzel, weight = FontWeight.Normal,   variationSettings = wght(400)),
    Font(R.font.cinzel, weight = FontWeight.SemiBold, variationSettings = wght(600)),
    Font(R.font.cinzel, weight = FontWeight.Bold,     variationSettings = wght(700)),
)

val BibleTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = CinzelFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp, letterSpacing = 1.5.sp
    ),
    displayMedium = TextStyle(
        fontFamily = CinzelFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp, letterSpacing = 1.2.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = CinzelFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, letterSpacing = 1.sp
    ),
    titleLarge = TextStyle(
        fontFamily = LoraFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontFamily = LoraFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp
    ),
    titleSmall = TextStyle(
        fontFamily = LoraFamily, fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = LoraFamily, fontWeight = FontWeight.Normal,
        fontSize = 18.sp, lineHeight = 30.sp, letterSpacing = 0.2.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = LoraFamily, fontWeight = FontWeight.Normal,
        fontSize = 15.sp, lineHeight = 24.sp
    ),
    bodySmall = TextStyle(
        fontFamily = LoraFamily, fontWeight = FontWeight.Normal,
        fontSize = 13.sp, lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = CinzelFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp, letterSpacing = 1.8.sp
    ),
    labelMedium = TextStyle(
        fontFamily = LoraFamily, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = LoraFamily, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, letterSpacing = 0.5.sp
    ),
)
