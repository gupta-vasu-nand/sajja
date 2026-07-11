package com.vng.sajja.ui.utils

import androidx.compose.ui.graphics.Color

fun android.graphics.Color.toComposeColor(): Color {
    return Color(
        red(), green(), blue(), alpha()
    )
}
