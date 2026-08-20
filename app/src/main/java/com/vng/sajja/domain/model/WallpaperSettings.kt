package com.vng.sajja.domain.model

import android.graphics.Color

enum class ClockType {
    ROMAN, ARABIC, DIGITAL, MINIMALIST
}

enum class BackgroundType {
    SOLID, GRADIENT, IMAGE
}

enum class ClockPosition {
    CENTER, TOP_CENTER, BOTTOM_CENTER, TOP_LEFT, TOP_RIGHT
}

enum class ParticleType {
    NONE, SNOW, BUBBLES, STARS, FIREFLIES, RAIN, CUSTOM
}

enum class DigitalAnimType {
    NONE, SLIDE, FADE, BOUNCE
}

data class WallpaperSettings(
    val clockType: ClockType = ClockType.ROMAN,
    val backgroundType: BackgroundType = BackgroundType.SOLID,
    val backgroundColor: Int = Color.BLACK,
    val gradientStartColor: Int = Color.BLACK,
    val gradientEndColor: Int = Color.DKGRAY,
    val clockSize: Float = 0.8f,
    val showBorder: Boolean = true,
    val borderColor: Int = Color.DKGRAY,
    val borderWidth: Float = 10f,
    val showNumerals: Boolean = true,
    val numeralColor: Int = Color.WHITE,
    val numeralSize: Float = 42f,
    val hourHandColor: Int = Color.WHITE,
    val hourHandWidth: Float = 12f,
    val minuteHandColor: Int = Color.WHITE,
    val minuteHandWidth: Float = 8f,
    val secondHandColor: Int = Color.RED,
    val secondHandWidth: Float = 4f,
    val showSecondHand: Boolean = true,
    val smoothSecondHand: Boolean = false,
    val centerKnobColor: Int = Color.WHITE,
    val centerKnobRadius: Float = 15f,
    val centerRingColor: Int = Color.DKGRAY,
    val centerRingWidth: Float = 4f,
    val showDate: Boolean = true,
    val dateColor: Int = Color.LTGRAY,
    val dateSize: Float = 36f,
    val showDay: Boolean = true,
    val dayColor: Int = Color.LTGRAY,
    val daySize: Float = 32f,
    val backgroundImageUri: String? = null,
    val clockPosition: ClockPosition = ClockPosition.CENTER,
    val showBottomText: Boolean = true,
    val clockOffsetX: Float = 0f,
    val clockOffsetY: Float = 0f,
    val use24HourFormat: Boolean = false,
    val showDigitalClockPlate: Boolean = false,
    val digitalClockPlateOpacity: Float = 0.3f,
    val digitalClockFontIndex: Int = 0,
    val particleType: ParticleType = ParticleType.NONE,
    val particleSpeed: Float = 1.0f,
    val particleCount: Int = 40,
    val digitalAnimType: DigitalAnimType = DigitalAnimType.NONE,
    val digitalAnimDuration: Long = 300L,
    val customParticleShape: String = "CIRCLE",
    val customParticleText: String = "✨",
    val customParticleDirection: String = "UP",
    val customParticleColor: Int = Color.WHITE
)
