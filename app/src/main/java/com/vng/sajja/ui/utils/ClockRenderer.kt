package com.vng.sajja.ui.utils

import android.graphics.*
import com.vng.sajja.domain.model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

object ClockRenderer {

    private val bgPaint = Paint()
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val numeralPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.SERIF
    }
    private val hourPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }
    private val minutePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }
    private val secondPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }
    private val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }
    private val centerKnobPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val centerRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val digitalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }

    private var lastTimeStr = ""
    private val charAnimStart = LongArray(10)
    private val charPrevValue = CharArray(10)
    private val charNextValue = CharArray(10)

    fun draw(
        canvas: Canvas,
        s: WallpaperSettings,
        time: Calendar,
        loadBitmap: (String) -> Bitmap?
    ) {
        drawBackground(canvas, s, loadBitmap)
        ParticleEffects.draw(canvas, s)

        val cx = canvas.width / 2f
        val cy = canvas.height / 2f
        val baseRadius = min(cx, cy)
        val radius = baseRadius * s.clockSize * 0.6f

        if (s.clockType == ClockType.DIGITAL) {
            drawDigitalClock(canvas, s, time)
        } else {
            if (s.showBorder) {
                borderPaint.color = s.borderColor
                borderPaint.strokeWidth = s.borderWidth
                canvas.drawCircle(cx, cy, radius, borderPaint)
            }

            if (s.showNumerals) {
                when (s.clockType) {
                    ClockType.ROMAN -> drawRomanNumerals(canvas, cx, cy, radius, s)
                    ClockType.ARABIC -> drawArabicNumerals(canvas, cx, cy, radius, s)
                    ClockType.MINIMALIST -> drawMinimalistTicks(canvas, cx, cy, radius, s)
                    else -> {}
                }
            }

            drawHands(canvas, cx, cy, radius, s, time)
            drawCenterKnob(canvas, cx, cy, s)
            drawDate(canvas, cx, cy, radius, s, time)
        }
    }

    private fun drawBackground(canvas: Canvas, s: WallpaperSettings, loadBitmap: (String) -> Bitmap?) {
        when (s.backgroundType) {
            BackgroundType.SOLID -> {
                canvas.drawColor(s.backgroundColor)
            }
            BackgroundType.GRADIENT -> {
                bgPaint.shader = LinearGradient(
                    0f, 0f,
                    canvas.width.toFloat(), canvas.height.toFloat(),
                    s.gradientStartColor,
                    s.gradientEndColor,
                    Shader.TileMode.CLAMP
                )
                canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), bgPaint)
                bgPaint.shader = null
            }
            BackgroundType.IMAGE -> {
                canvas.drawColor(s.backgroundColor)
                s.backgroundImageUri?.let { uri ->
                    val bitmap = loadBitmap(uri)
                    if (bitmap != null) {
                        val src = Rect(0, 0, bitmap.width, bitmap.height)
                        val dst = Rect(0, 0, canvas.width, canvas.height)
                        canvas.drawBitmap(bitmap, src, dst, null)
                    }
                }
            }
        }
    }



    private fun drawRomanNumerals(canvas: Canvas, cx: Float, cy: Float, radius: Float, s: WallpaperSettings) {
        numeralPaint.color = s.numeralColor
        numeralPaint.textSize = s.numeralSize

        val romans = arrayOf(
            "XII", "I", "II", "III", "IV", "V",
            "VI", "VII", "VIII", "IX", "X", "XI"
        )
        val borderPadding = if (s.showBorder) s.borderWidth / 2 else 0f
        val innerRadius = radius - borderPadding - (s.numeralSize / 3)

        for (i in romans.indices) {
            val angle = Math.toRadians((i * 30 - 90).toDouble())
            val x = cx + cos(angle) * innerRadius * 0.9
            val y = cy + sin(angle) * innerRadius * 0.9 + numeralPaint.textSize / 3
            canvas.drawText(romans[i], x.toFloat(), y.toFloat(), numeralPaint)
        }
    }

    private fun drawArabicNumerals(canvas: Canvas, cx: Float, cy: Float, radius: Float, s: WallpaperSettings) {
        numeralPaint.color = s.numeralColor
        numeralPaint.textSize = s.numeralSize

        val arabics = arrayOf(
            "12", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "10", "11"
        )
        val borderPadding = if (s.showBorder) s.borderWidth / 2 else 0f
        val innerRadius = radius - borderPadding - (s.numeralSize / 3)

        for (i in arabics.indices) {
            val angle = Math.toRadians((i * 30 - 90).toDouble())
            val x = cx + cos(angle) * innerRadius * 0.9
            val y = cy + sin(angle) * innerRadius * 0.9 + numeralPaint.textSize / 3
            canvas.drawText(arabics[i], x.toFloat(), y.toFloat(), numeralPaint)
        }
    }

    private fun drawMinimalistTicks(canvas: Canvas, cx: Float, cy: Float, radius: Float, s: WallpaperSettings) {
        numeralPaint.color = s.numeralColor
        val borderPadding = if (s.showBorder) s.borderWidth / 2 else 0f
        val innerRadius = radius - borderPadding

        for (i in 0 until 12) {
            val angle = Math.toRadians((i * 30 - 90).toDouble())
            val isMajor = i % 3 == 0
            val tickRadius = if (isMajor) 10f else 5f
            val x = cx + cos(angle) * innerRadius * 0.9
            val y = cy + sin(angle) * innerRadius * 0.9
            canvas.drawCircle(x.toFloat(), y.toFloat(), tickRadius, numeralPaint)
        }
    }

    private fun drawHands(canvas: Canvas, cx: Float, cy: Float, radius: Float, s: WallpaperSettings, time: Calendar) {
        val sec = time.get(Calendar.SECOND) + time.get(Calendar.MILLISECOND) / 1000f
        val minVal = time.get(Calendar.MINUTE)
        val hr = time.get(Calendar.HOUR) % 12

        hourPaint.color = s.hourHandColor
        hourPaint.strokeWidth = s.hourHandWidth

        minutePaint.color = s.minuteHandColor
        minutePaint.strokeWidth = s.minuteHandWidth

        secondPaint.color = s.secondHandColor
        secondPaint.strokeWidth = s.secondHandWidth

        val borderPadding = if (s.showBorder) s.borderWidth / 2 else 0f
        val innerRadius = radius - borderPadding - (s.numeralSize / 3)

        val hourAngle = Math.toRadians(((hr + minVal / 60f) * 30 - 90).toDouble())
        val minAngle = Math.toRadians((minVal * 6 - 90).toDouble())
        val secAngle = Math.toRadians(sec * 6.0 - 90)

        canvas.drawLine(
            cx, cy,
            (cx + cos(hourAngle) * innerRadius * 0.5).toFloat(),
            (cy + sin(hourAngle) * innerRadius * 0.5).toFloat(),
            hourPaint
        )

        canvas.drawLine(
            cx, cy,
            (cx + cos(minAngle) * innerRadius * 0.7).toFloat(),
            (cy + sin(minAngle) * innerRadius * 0.7).toFloat(),
            minutePaint
        )

        if (s.showSecondHand) {
            canvas.drawLine(
                cx, cy,
                (cx + cos(secAngle) * innerRadius * 0.85).toFloat(),
                (cy + sin(secAngle) * innerRadius * 0.85).toFloat(),
                secondPaint
            )
        }
    }

    private fun drawCenterKnob(canvas: Canvas, cx: Float, cy: Float, s: WallpaperSettings) {
        centerRingPaint.color = s.centerRingColor
        centerRingPaint.strokeWidth = s.centerRingWidth
        canvas.drawCircle(cx, cy, s.centerKnobRadius + s.centerRingWidth, centerRingPaint)

        centerRingPaint.color = s.centerRingColor
        centerRingPaint.strokeWidth = s.centerRingWidth / 2
        canvas.drawCircle(cx, cy, s.centerKnobRadius - s.centerRingWidth, centerRingPaint)

        centerKnobPaint.color = s.centerKnobColor
        canvas.drawCircle(cx, cy, s.centerKnobRadius, centerKnobPaint)
    }

    private fun drawDigitalClock(
        canvas: Canvas,
        s: WallpaperSettings,
        time: Calendar
    ) {
        val clockX = when (s.clockPosition) {
            ClockPosition.TOP_LEFT -> canvas.width * 0.08f
            ClockPosition.TOP_RIGHT -> canvas.width * 0.92f
            else -> canvas.width / 2f
        }

        val clockY = when (s.clockPosition) {
            ClockPosition.TOP_LEFT, ClockPosition.TOP_CENTER, ClockPosition.TOP_RIGHT -> canvas.height * 0.18f
            ClockPosition.BOTTOM_CENTER -> canvas.height * 0.72f
            else -> canvas.height / 2f
        }

        // Apply custom typeface
        digitalPaint.typeface = when (s.digitalClockFontIndex) {
            1 -> Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            2 -> Typeface.create(Typeface.SERIF, Typeface.BOLD)
            3 -> Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            else -> Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }

        digitalPaint.color = s.numeralColor
        digitalPaint.textSize = s.numeralSize * 3.5f * s.clockSize

        val formatPattern = if (s.use24HourFormat) {
            if (s.showSecondHand) "HH:mm:ss" else "HH:mm"
        } else {
            if (s.showSecondHand) "hh:mm:ss" else "hh:mm"
        }
        val timeSdf = SimpleDateFormat(formatPattern, Locale.getDefault())
        val amPmSdf = SimpleDateFormat("a", Locale.getDefault())

        val timeText = timeSdf.format(time.time)
        val amPmText = amPmSdf.format(time.time)

        val timeWidth = digitalPaint.measureText(timeText)
        val amPmPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = s.numeralColor
            alpha = 178 // 70% opacity
            textSize = digitalPaint.textSize * 0.45f
            typeface = digitalPaint.typeface
            textAlign = Paint.Align.LEFT
        }
        val amPmWidth = if (s.use24HourFormat) 0f else amPmPaint.measureText(amPmText)
        val spacing = if (s.use24HourFormat) 0f else 15f

        val amPmY = clockY + s.clockOffsetY - (digitalPaint.textSize * 0.12f)

        // Draw Glass Card backplate if enabled
        if (s.showDigitalClockPlate) {
            val maxTextWidth = if (s.showBottomText) {
                val dateSdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val dateWidth = datePaint.measureText(dateSdf.format(Calendar.getInstance().time))
                maxOf(timeWidth + spacing + amPmWidth, dateWidth)
            } else {
                timeWidth + spacing + amPmWidth
            }

            val textHeight = digitalPaint.textSize
            val bottomTextHeight = if (s.showBottomText) {
                val count = (if (s.showDate) 1 else 0) + (if (s.showDay) 1 else 0)
                count * (s.dateSize + 25f) + 40f
            } else {
                0f
            }

            val totalPlateWidth = maxTextWidth + 120f
            val totalPlateHeight = textHeight + bottomTextHeight + 100f

            val platePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                alpha = (s.digitalClockPlateOpacity * 255).toInt()
                style = Paint.Style.FILL
            }
            val plateStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                alpha = ((s.digitalClockPlateOpacity + 0.15f).coerceAtMost(1f) * 255).toInt()
                style = Paint.Style.STROKE
                strokeWidth = 3f
            }
            
            val plateCenterX = when (s.clockPosition) {
                ClockPosition.TOP_LEFT -> clockX + s.clockOffsetX + (maxTextWidth / 2f)
                ClockPosition.TOP_RIGHT -> clockX + s.clockOffsetX - (maxTextWidth / 2f)
                else -> clockX + s.clockOffsetX
            }

            val left = plateCenterX - (totalPlateWidth / 2f)
            val top = clockY + s.clockOffsetY - (textHeight / 2f) - 40f
            val right = left + totalPlateWidth
            val bottom = top + totalPlateHeight
            
            val rect = RectF(left, top, right, bottom)
            canvas.drawRoundRect(rect, 40f, 40f, platePaint)
            canvas.drawRoundRect(rect, 40f, 40f, plateStrokePaint)
        }

        // Initialize digit animation state
        val currentTime = System.currentTimeMillis()
        val animDuration = s.digitalAnimDuration.coerceIn(100L, 1000L)

        if (lastTimeStr.length != timeText.length) {
            lastTimeStr = timeText
            for (i in 0 until timeText.length) {
                charAnimStart[i] = 0L
                charPrevValue[i] = timeText[i]
                charNextValue[i] = timeText[i]
            }
        }

        for (i in 0 until timeText.length) {
            if (timeText[i] != lastTimeStr.getOrElse(i) { timeText[i] }) {
                charPrevValue[i] = lastTimeStr.getOrElse(i) { timeText[i] }
                charNextValue[i] = timeText[i]
                charAnimStart[i] = currentTime
            }
        }
        lastTimeStr = timeText

        // Calculate start position
        val startX = when (s.clockPosition) {
            ClockPosition.TOP_LEFT -> clockX + s.clockOffsetX
            ClockPosition.TOP_RIGHT -> clockX + s.clockOffsetX - timeWidth - (if (s.use24HourFormat) 0f else amPmWidth + spacing)
            else -> clockX + s.clockOffsetX - (if (s.use24HourFormat) timeWidth / 2f else (timeWidth + spacing + amPmWidth) / 2f)
        }

        // Draw Time text character by character
        var currentX = startX
        digitalPaint.textAlign = Paint.Align.CENTER // Center align each char to its slot

        for (i in 0 until timeText.length) {
            val charStr = timeText[i].toString()
            val charWidth = digitalPaint.measureText(charStr)
            val slotCenterX = currentX + (charWidth / 2f)

            val startTime = charAnimStart[i]
            val elapsed = currentTime - startTime

            if (s.digitalAnimType == DigitalAnimType.NONE || startTime == 0L || elapsed >= animDuration) {
                canvas.drawText(charStr, slotCenterX, clockY + s.clockOffsetY + (digitalPaint.textSize / 3), digitalPaint)
            } else {
                val t = (elapsed.toFloat() / animDuration).coerceIn(0f, 1f)
                val prevStr = charPrevValue[i].toString()
                val nextStr = charNextValue[i].toString()

                when (s.digitalAnimType) {
                    DigitalAnimType.FADE -> {
                        val oldPaint = Paint(digitalPaint).apply { alpha = ((1f - t) * 255).toInt() }
                        canvas.drawText(prevStr, slotCenterX, clockY + s.clockOffsetY + (digitalPaint.textSize / 3), oldPaint)

                        val newPaint = Paint(digitalPaint).apply { alpha = (t * 255).toInt() }
                        canvas.drawText(nextStr, slotCenterX, clockY + s.clockOffsetY + (digitalPaint.textSize / 3), newPaint)
                    }
                    DigitalAnimType.SLIDE -> {
                        val oldPaint = Paint(digitalPaint).apply { alpha = ((1f - t) * 255).toInt() }
                        val oldY = clockY + s.clockOffsetY + (digitalPaint.textSize / 3) - (t * digitalPaint.textSize * 0.8f)
                        canvas.drawText(prevStr, slotCenterX, oldY, oldPaint)

                        val newPaint = Paint(digitalPaint).apply { alpha = (t * 255).toInt() }
                        val newY = clockY + s.clockOffsetY + (digitalPaint.textSize / 3) + ((1f - t) * digitalPaint.textSize * 0.8f)
                        canvas.drawText(nextStr, slotCenterX, newY, newPaint)
                    }
                    DigitalAnimType.BOUNCE -> {
                        val oldScale = 1f - t
                        canvas.save()
                        canvas.scale(oldScale, oldScale, slotCenterX, clockY + s.clockOffsetY)
                        canvas.drawText(prevStr, slotCenterX, clockY + s.clockOffsetY + (digitalPaint.textSize / 3), digitalPaint)
                        canvas.restore()

                        val bounceScale = if (t < 0.6f) {
                            (t / 0.6f) * 1.15f
                        } else {
                            1.15f - ((t - 0.6f) / 0.4f) * 0.15f
                        }
                        canvas.save()
                        canvas.scale(bounceScale, bounceScale, slotCenterX, clockY + s.clockOffsetY)
                        canvas.drawText(nextStr, slotCenterX, clockY + s.clockOffsetY + (digitalPaint.textSize / 3), digitalPaint)
                        canvas.restore()
                    }
                    else -> {
                        canvas.drawText(charStr, slotCenterX, clockY + s.clockOffsetY + (digitalPaint.textSize / 3), digitalPaint)
                    }
                }
            }
            currentX += charWidth
        }

        // Draw AM/PM
        if (!s.use24HourFormat) {
            canvas.drawText(amPmText, startX + timeWidth + spacing, amPmY, amPmPaint)
        }

        // Draw date and day under digital clock
        if (s.showBottomText) {
            val cal = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            
            val bottomTextX = clockX + s.clockOffsetX
            val align = when (s.clockPosition) {
                ClockPosition.TOP_LEFT -> Paint.Align.LEFT
                ClockPosition.TOP_RIGHT -> Paint.Align.RIGHT
                else -> Paint.Align.CENTER
            }
            
            datePaint.textAlign = align
            val textY = clockY + s.clockOffsetY + (digitalPaint.textSize * 0.65f) + 30f

            if (s.showDate) {
                datePaint.color = s.dateColor
                datePaint.textSize = s.dateSize

                val dateText = dateFormat.format(cal.time)
                canvas.drawText(dateText, bottomTextX, textY, datePaint)

                if (s.showDay) {
                    datePaint.color = s.dayColor
                    datePaint.textSize = s.daySize
                    val dayText = dayFormat.format(cal.time)
                    canvas.drawText(dayText, bottomTextX, textY + s.dateSize + 25f, datePaint)
                }
            } else if (s.showDay) {
                datePaint.color = s.dayColor
                datePaint.textSize = s.daySize
                val dayText = dayFormat.format(cal.time)
                canvas.drawText(dayText, bottomTextX, textY, datePaint)
            }
        }
    }

    private fun drawDate(canvas: Canvas, cx: Float, cy: Float, radius: Float, s: WallpaperSettings, time: Calendar) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val textY = cy + radius + 60f

        if (s.showDate) {
            datePaint.color = s.dateColor
            datePaint.textSize = s.dateSize

            val dateText = dateFormat.format(time.time)
            canvas.drawText(dateText, cx, textY, datePaint)

            if (s.showDay) {
                datePaint.color = s.dayColor
                datePaint.textSize = s.daySize
                val dayText = dayFormat.format(time.time)
                canvas.drawText(dayText, cx, textY + s.dateSize + 25f, datePaint)
            }
        } else if (s.showDay) {
            datePaint.color = s.dayColor
            datePaint.textSize = s.daySize
            val dayText = dayFormat.format(time.time)
            canvas.drawText(dayText, cx, textY, datePaint)
        }
    }
}
