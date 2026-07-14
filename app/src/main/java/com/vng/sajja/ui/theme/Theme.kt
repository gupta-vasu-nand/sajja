package com.vng.sajja.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.vng.sajja.domain.model.AppSettings
import com.vng.sajja.domain.model.AppThemeMode

import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt

fun getContrastingTextColor(backgroundColor: Color): Color {
    val red = backgroundColor.red
    val green = backgroundColor.green
    val blue = backgroundColor.blue
    val luminance = 0.299f * red + 0.587f * green + 0.114f * blue
    return if (luminance > 0.5f) Color.Black else Color.White
}

@Composable
fun SajjaTheme(
    appSettings: AppSettings,
    content: @Composable () -> Unit
) {
    val darkTheme = when (appSettings.themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val primaryColor = Color(appSettings.activeColorHex.toColorInt())

    // Derive secondary and tertiary colors dynamically using HSV color shifts
    val temp = FloatArray(3)
    android.graphics.Color.colorToHSV(primaryColor.toArgb(), temp)
    val hue = temp[0]
    val saturation = temp[1]
    val value = temp[2]

    val secondaryHue = (hue + 30f) % 360f
    val secondarySat = (saturation * 0.75f).coerceIn(0.2f, 0.8f)
    val secondaryVal = if (darkTheme) 0.85f else 0.5f
    val secondaryColor = Color(android.graphics.Color.HSVToColor(floatArrayOf(secondaryHue, secondarySat, secondaryVal)))

    val tertiaryHue = (hue + 120f) % 360f
    val tertiarySat = (saturation * 0.7f).coerceIn(0.2f, 0.8f)
    val tertiaryVal = if (darkTheme) 0.8f else 0.55f
    val tertiaryColor = Color(android.graphics.Color.HSVToColor(floatArrayOf(tertiaryHue, tertiarySat, tertiaryVal)))

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            tertiary = tertiaryColor,
            onPrimary = getContrastingTextColor(primaryColor),
            onSecondary = getContrastingTextColor(secondaryColor),
            onTertiary = getContrastingTextColor(tertiaryColor),
            background = Color.Black,
            surface = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White,
            surfaceContainer = Color(0xFF121212),
            surfaceContainerLow = Color(0xFF1E1E1E),
            outline = Color(0xFF2C2C2C),
            primaryContainer = primaryColor.copy(alpha = 0.3f),
            onPrimaryContainer = Color.White
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            tertiary = tertiaryColor,
            onPrimary = getContrastingTextColor(primaryColor),
            onSecondary = getContrastingTextColor(secondaryColor),
            onTertiary = getContrastingTextColor(tertiaryColor),
            background = Color.White,
            surface = Color.White,
            onBackground = Color.Black,
            onSurface = Color.Black,
            surfaceContainer = Color(0xFFF9F9F9),
            surfaceContainerLow = Color(0xFFE5E5E5),
            outline = Color(0xFFCCCCCC),
            primaryContainer = primaryColor.copy(alpha = 0.15f),
            onPrimaryContainer = primaryColor
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}