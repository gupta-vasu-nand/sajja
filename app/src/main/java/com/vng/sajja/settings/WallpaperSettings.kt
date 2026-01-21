package com.vng.sajja.settings

import android.graphics.Color

data class WallpaperSettings(
    val backgroundType: BackgroundType = BackgroundType.SOLID,
    val backgroundColor: Int = Color.BLACK,
    val gradientStartColor: Int = Color.BLACK,
    val gradientEndColor: Int = Color.DKGRAY,
    val collageImages: List<CollageImage> = emptyList(),
    val collageLayout: CollageLayout = CollageLayout.GRID,
    val imageSpacing: Float = 20f,
    val collageOpacity: Float = 1.0f,
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
    val daySize: Float = 32f
) {
    companion object {
        fun createCollageTemplate(template: CollageTemplate): WallpaperSettings {
            return when (template) {
                CollageTemplate.MEMORIES -> WallpaperSettings(
                    backgroundType = BackgroundType.COLLAGE,
                    backgroundColor = Color.parseColor("#1A1A2E"),
                    collageOpacity = 0.9f,
                    clockSize = 0.7f,
                    showBorder = false,
                    numeralColor = Color.WHITE,
                    hourHandColor = Color.WHITE,
                    minuteHandColor = Color.WHITE,
                    secondHandColor = Color.parseColor("#FF6B8B"),
                    centerKnobColor = Color.parseColor("#FF6B8B"),
                    centerRingColor = Color.WHITE,
                    dateColor = Color.WHITE,
                    dayColor = Color.WHITE
                )
                CollageTemplate.TRAVEL -> WallpaperSettings(
                    backgroundType = BackgroundType.COLLAGE,
                    backgroundColor = Color.parseColor("#0F3460"),
                    collageOpacity = 0.85f,
                    clockSize = 0.75f,
                    showBorder = true,
                    borderColor = Color.WHITE,
                    borderWidth = 4f,
                    numeralColor = Color.WHITE,
                    hourHandColor = Color.WHITE,
                    minuteHandColor = Color.WHITE,
                    secondHandColor = Color.parseColor("#00FFAB"),
                    centerKnobColor = Color.parseColor("#00FFAB"),
                    centerRingColor = Color.WHITE,
                    dateColor = Color.WHITE,
                    dayColor = Color.WHITE
                )
                CollageTemplate.MINIMAL -> WallpaperSettings(
                    backgroundType = BackgroundType.COLLAGE,
                    backgroundColor = Color.BLACK,
                    collageOpacity = 0.8f,
                    clockSize = 0.65f,
                    showBorder = false,
                    numeralColor = Color.WHITE,
                    hourHandColor = Color.WHITE,
                    minuteHandColor = Color.WHITE,
                    secondHandColor = Color.RED,
                    centerKnobColor = Color.WHITE,
                    centerRingColor = Color.WHITE,
                    dateColor = Color.LTGRAY,
                    dayColor = Color.LTGRAY
                )
            }
        }
    }
}

enum class BackgroundType {
    SOLID, GRADIENT, COLLAGE
}

enum class CollageLayout {
    GRID, MASONRY, CENTER_FOCUS, SPIRAL, RANDOM
}

enum class CollageTemplate {
    MEMORIES, TRAVEL, MINIMAL
}

enum class ScaleType {
    CENTER_CROP, CENTER_INSIDE, FIT_CENTER, ORIGINAL
}

data class CollageImage(
    val uri: String,
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 0.3f,
    val height: Float = 0.3f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f,
    val zIndex: Int = 0,
    val scaleType: ScaleType = ScaleType.CENTER_CROP
)