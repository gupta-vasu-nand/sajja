package com.vng.sajja.data.repository

import android.content.Context
import android.graphics.Color
import androidx.core.content.edit
import com.vng.sajja.domain.model.*
import com.vng.sajja.domain.repository.WallpaperSettingsRepository

class WallpaperSettingsRepositoryImpl(context: Context) : WallpaperSettingsRepository {

    private val prefs = context.getSharedPreferences("roman_clock_settings", Context.MODE_PRIVATE)

    override fun load(): WallpaperSettings {
        return WallpaperSettings(
            clockType = ClockType.valueOf(
                prefs.getString("clock_type", ClockType.ROMAN.name)!!
            ),
            backgroundType = BackgroundType.valueOf(
                prefs.getString("bg_type", BackgroundType.SOLID.name)!!
            ),
            backgroundColor = prefs.getInt("bg_color", Color.BLACK),
            gradientStartColor = prefs.getInt("grad_start", Color.BLACK),
            gradientEndColor = prefs.getInt("grad_end", Color.DKGRAY),
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
            daySize = prefs.getFloat("day_size", 32f),
            backgroundImageUri = prefs.getString("bg_image_uri", null),
            clockPosition = ClockPosition.valueOf(
                prefs.getString("clock_position", ClockPosition.CENTER.name)!!
            ),
            showBottomText = prefs.getBoolean("show_bottom_text", true),
            clockOffsetX = prefs.getFloat("clock_offset_x", 0f),
            clockOffsetY = prefs.getFloat("clock_offset_y", 0f),
            use24HourFormat = prefs.getBoolean("use_24_hour", false),
            showDigitalClockPlate = prefs.getBoolean("show_digital_plate", false),
            digitalClockPlateOpacity = prefs.getFloat("digital_plate_opacity", 0.3f),
            digitalClockFontIndex = prefs.getInt("digital_clock_font", 0),
            particleType = ParticleType.valueOf(
                prefs.getString("particle_type", ParticleType.NONE.name)!!
            ),
            particleSpeed = prefs.getFloat("particle_speed", 1.0f),
            particleCount = prefs.getInt("particle_count", 40),
            digitalAnimType = DigitalAnimType.valueOf(
                prefs.getString("digital_anim_type", DigitalAnimType.NONE.name)!!
            ),
            digitalAnimDuration = prefs.getLong("digital_anim_duration", 300L)
        )
    }

    override fun save(s: WallpaperSettings) {
        prefs.edit {
            putString("clock_type", s.clockType.name)
            putString("bg_type", s.backgroundType.name)
            putInt("bg_color", s.backgroundColor)
            putInt("grad_start", s.gradientStartColor)
            putInt("grad_end", s.gradientEndColor)
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
            putString("bg_image_uri", s.backgroundImageUri)
            putString("clock_position", s.clockPosition.name)
            putBoolean("show_bottom_text", s.showBottomText)
            putFloat("clock_offset_x", s.clockOffsetX)
            putFloat("clock_offset_y", s.clockOffsetY)
            putBoolean("use_24_hour", s.use24HourFormat)
            putBoolean("show_digital_plate", s.showDigitalClockPlate)
            putFloat("digital_plate_opacity", s.digitalClockPlateOpacity)
            putInt("digital_clock_font", s.digitalClockFontIndex)
            putString("particle_type", s.particleType.name)
            putFloat("particle_speed", s.particleSpeed)
            putInt("particle_count", s.particleCount)
            putString("digital_anim_type", s.digitalAnimType.name)
            putLong("digital_anim_duration", s.digitalAnimDuration)
        }
    }
}
