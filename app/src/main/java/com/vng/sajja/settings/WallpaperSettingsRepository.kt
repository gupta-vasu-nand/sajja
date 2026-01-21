package com.vng.sajja.settings

import android.content.Context
import android.graphics.Color
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WallpaperSettingsRepository(context: Context) {

    private val prefs =
        context.getSharedPreferences("roman_clock_settings", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun load(): WallpaperSettings {
        val collageJson = prefs.getString("collage_images", "[]")
        val collageType = object : TypeToken<List<CollageImage>>() {}.type
        val collageImages = gson.fromJson<List<CollageImage>>(collageJson, collageType) ?: emptyList()

        return WallpaperSettings(
            backgroundType = BackgroundType.valueOf(
                prefs.getString("bg_type", BackgroundType.SOLID.name)!!
            ),
            backgroundColor = prefs.getInt("bg_color", Color.BLACK),
            gradientStartColor = prefs.getInt("grad_start", Color.BLACK),
            gradientEndColor = prefs.getInt("grad_end", Color.DKGRAY),
            collageImages = collageImages,
            collageLayout = CollageLayout.valueOf(
                prefs.getString("collage_layout", CollageLayout.GRID.name)!!
            ),
            imageSpacing = prefs.getFloat("image_spacing", 20f),
            collageOpacity = prefs.getFloat("collage_opacity", 1.0f),
            clockSize = prefs.getFloat("clock_size", 0.8f),
            showBorder = prefs.getBoolean("show_border", true),
            borderColor = prefs.getInt("border_color", Color.DKGRAY),
            borderWidth = prefs.getFloat("border_width", 10f),
            showNumerals = prefs.getBoolean("show_nums", true),
            numeralColor = prefs.getInt("num_color", Color.WHITE),
            numeralSize = prefs.getFloat("num_size", 42f),
            hourHandColor = prefs.getInt("hour_color", Color.WHITE),
            hourHandWidth = prefs.getFloat("hour_width", 12f),
            minuteHandColor = prefs.getInt("minute_color", Color.WHITE),
            minuteHandWidth = prefs.getFloat("minute_width", 8f),
            secondHandColor = prefs.getInt("second_color", Color.RED),
            secondHandWidth = prefs.getFloat("second_width", 4f),
            showSecondHand = prefs.getBoolean("show_second", true),
            smoothSecondHand = prefs.getBoolean("smooth_second", false),
            centerKnobColor = prefs.getInt("knob_color", Color.WHITE),
            centerKnobRadius = prefs.getFloat("knob_radius", 15f),
            centerRingColor = prefs.getInt("ring_color", Color.DKGRAY),
            centerRingWidth = prefs.getFloat("ring_width", 4f),
            showDate = prefs.getBoolean("show_date", true),
            dateColor = prefs.getInt("date_color", Color.LTGRAY),
            dateSize = prefs.getFloat("date_size", 36f),
            showDay = prefs.getBoolean("show_day", true),
            dayColor = prefs.getInt("day_color", Color.LTGRAY),
            daySize = prefs.getFloat("day_size", 32f)
        )
    }

    fun save(s: WallpaperSettings) {
        prefs.edit {
            putString("bg_type", s.backgroundType.name)
            putInt("bg_color", s.backgroundColor)
            putInt("grad_start", s.gradientStartColor)
            putInt("grad_end", s.gradientEndColor)
            putString("collage_images", gson.toJson(s.collageImages))
            putString("collage_layout", s.collageLayout.name)
            putFloat("image_spacing", s.imageSpacing)
            putFloat("collage_opacity", s.collageOpacity)
            putFloat("clock_size", s.clockSize)
            putBoolean("show_border", s.showBorder)
            putInt("border_color", s.borderColor)
            putFloat("border_width", s.borderWidth)
            putBoolean("show_nums", s.showNumerals)
            putInt("num_color", s.numeralColor)
            putFloat("num_size", s.numeralSize)
            putInt("hour_color", s.hourHandColor)
            putFloat("hour_width", s.hourHandWidth)
            putInt("minute_color", s.minuteHandColor)
            putFloat("minute_width", s.minuteHandWidth)
            putInt("second_color", s.secondHandColor)
            putFloat("second_width", s.secondHandWidth)
            putBoolean("show_second", s.showSecondHand)
            putBoolean("smooth_second", s.smoothSecondHand)
            putInt("knob_color", s.centerKnobColor)
            putFloat("knob_radius", s.centerKnobRadius)
            putInt("ring_color", s.centerRingColor)
            putFloat("ring_width", s.centerRingWidth)
            putBoolean("show_date", s.showDate)
            putInt("date_color", s.dateColor)
            putFloat("date_size", s.dateSize)
            putBoolean("show_day", s.showDay)
            putInt("day_color", s.dayColor)
            putFloat("day_size", s.daySize)
        }
    }
}