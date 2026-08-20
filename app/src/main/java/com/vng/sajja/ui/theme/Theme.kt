package com.vng.sajja.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import com.vng.sajja.domain.model.AppSettings
import com.vng.sajja.domain.model.AppThemeMode
import kotlin.math.pow

/**
 * Calculates a contrasting text color (either Dark or White) based on the background color,
 * taking into account WCAG relative luminance, alpha transparency, and system theme mode.
 */
fun getContrastingTextColor(
    backgroundColor: Color,
    isDarkTheme: Boolean = false
): Color {
    val alpha = backgroundColor.alpha

    // If background alpha is very small (translucent), the dominant visible background is the underlying system theme
    if (alpha < 0.35f) {
        return if (isDarkTheme) Color.White else Color(0xFF0D0D0D)
    }

    // Blend transparent background over system background for accurate luminance calculation
    val baseThemeBackground = if (isDarkTheme) Color(0xFF121212) else Color(0xFFFFFFFF)
    val effectiveColor = blendColor(backgroundColor, baseThemeBackground)

    // Calculate WCAG relative luminance
    val relativeLuminance = calculateRelativeLuminance(effectiveColor)

    // WCAG standard threshold: relative luminance > 0.45 requires dark text, otherwise white text.
    // For vibrant coral (#FC5E6A), relativeLuminance is ~0.30, evaluating cleanly to Color.White.
    return if (relativeLuminance > 0.45f) Color(0xFF0D0D0D) else Color.White
}

private fun blendColor(foreground: Color, background: Color): Color {
    val alpha = foreground.alpha.coerceIn(0f, 1f)
    val invAlpha = 1f - alpha

    val red = foreground.red * alpha + background.red * invAlpha
    val green = foreground.green * alpha + background.green * invAlpha
    val blue = foreground.blue * alpha + background.blue * invAlpha

    return Color(red = red, green = green, blue = blue, alpha = 1f)
}

private fun calculateRelativeLuminance(color: Color): Float {
    fun convert(channel: Float): Float {
        return if (channel <= 0.04045f) {
            channel / 12.92f
        } else {
            ((channel + 0.055f) / 1.055f).pow(2.4f)
        }
    }

    val r = convert(color.red)
    val g = convert(color.green)
    val b = convert(color.blue)

    return 0.2126f * r + 0.7152f * g + 0.0722f * b
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
            onPrimary = getContrastingTextColor(primaryColor, isDarkTheme = true),
            onSecondary = getContrastingTextColor(secondaryColor, isDarkTheme = true),
            onTertiary = getContrastingTextColor(tertiaryColor, isDarkTheme = true),
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
            onPrimary = getContrastingTextColor(primaryColor, isDarkTheme = false),
            onSecondary = getContrastingTextColor(secondaryColor, isDarkTheme = false),
            onTertiary = getContrastingTextColor(tertiaryColor, isDarkTheme = false),
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