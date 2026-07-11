package com.vng.sajja.domain.model

import android.graphics.Color

object ThemePresets {
    val Classic = WallpaperSettings()

    val AMOLED = WallpaperSettings(
        backgroundColor = Color.BLACK,
        showBorder = false,
        smoothSecondHand = true,
        centerKnobColor = Color.WHITE,
        centerRingColor = Color.WHITE,
        numeralColor = Color.WHITE,
        hourHandColor = Color.WHITE,
        minuteHandColor = Color.WHITE
    )

    val Sunset = WallpaperSettings(
        backgroundType = BackgroundType.GRADIENT,
        gradientStartColor = Color.parseColor("#FF512F"),
        gradientEndColor = Color.parseColor("#DD2476"),
        numeralColor = Color.WHITE,
        centerKnobColor = Color.WHITE,
        centerRingColor = Color.WHITE,
        hourHandColor = Color.WHITE,
        minuteHandColor = Color.WHITE
    )

    val Ocean = WallpaperSettings(
        backgroundType = BackgroundType.GRADIENT,
        gradientStartColor = Color.parseColor("#00B4DB"),
        gradientEndColor = Color.parseColor("#0083B0"),
        numeralColor = Color.WHITE,
        centerKnobColor = Color.WHITE,
        centerRingColor = Color.WHITE,
        hourHandColor = Color.WHITE,
        minuteHandColor = Color.WHITE
    )

    val Forest = WallpaperSettings(
        backgroundType = BackgroundType.GRADIENT,
        gradientStartColor = Color.parseColor("#0F2027"),
        gradientEndColor = Color.parseColor("#2C5364"),
        numeralColor = Color.WHITE,
        centerKnobColor = Color.WHITE,
        centerRingColor = Color.WHITE,
        hourHandColor = Color.WHITE,
        minuteHandColor = Color.WHITE
    )


}
