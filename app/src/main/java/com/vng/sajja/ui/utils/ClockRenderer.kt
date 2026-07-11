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
    private val collagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isDither = true
        isFilterBitmap = true
    }
    private val digitalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }

    fun draw(
        canvas: Canvas,
        s: WallpaperSettings,
        time: Calendar,
        loadBitmap: (String) -> Bitmap?
    ) {
        drawBackground(canvas, s, loadBitmap)

        val cx = canvas.width / 2f
        val cy = canvas.height / 2f
        val baseRadius = min(cx, cy)
        val radius = baseRadius * s.clockSize * 0.6f

        if (s.clockType == ClockType.DIGITAL) {
            drawDigitalClock(canvas, cx, cy, radius, s, time)
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
                        canvas.drawBitmap(bitmap, src, dst, collagePaint)
                    }
                }
            }
            BackgroundType.COLLAGE -> {
                canvas.drawColor(s.backgroundColor)
                drawCollage(canvas, s, loadBitmap)
            }
        }
    }

    private fun drawCollage(canvas: Canvas, s: WallpaperSettings, loadBitmap: (String) -> Bitmap?) {
        val collageImages = s.collageImages.sortedBy { it.zIndex }
        for (image in collageImages) {
            val bitmap = loadBitmap(image.uri) ?: continue
            collagePaint.alpha = (image.opacity * s.collageOpacity * 255).toInt()

            val x = image.x * canvas.width
            val y = image.y * canvas.height
            val width = image.width * canvas.width
            val height = image.height * canvas.height

            canvas.save()
            canvas.translate(x + width / 2, y + height / 2)
            canvas.rotate(image.rotation)

            val srcRect = Rect(0, 0, bitmap.width, bitmap.height)
            val dstRect = when (image.scaleType) {
                ScaleType.CENTER_CROP -> {
                    val bitmapRatio = bitmap.width.toFloat() / bitmap.height
                    val rectRatio = width / height
                    if (bitmapRatio > rectRatio) {
                        val scaledHeight = height
                        val scaledWidth = scaledHeight * bitmapRatio
                        val left = (width - scaledWidth) / 2
                        RectF(left, 0f, left + scaledWidth, scaledHeight)
                    } else {
                        val scaledWidth = width
                        val scaledHeight = scaledWidth / bitmapRatio
                        val top = (height - scaledHeight) / 2
                        RectF(0f, top, scaledWidth, top + scaledHeight)
                    }
                }
                ScaleType.CENTER_INSIDE, ScaleType.FIT_CENTER -> {
                    val bitmapRatio = bitmap.width.toFloat() / bitmap.height
                    val rectRatio = width / height
                    if (bitmapRatio > rectRatio) {
                        val scaledWidth = width
                        val scaledHeight = scaledWidth / bitmapRatio
                        val top = (height - scaledHeight) / 2
                        RectF(0f, top, scaledWidth, top + scaledHeight)
                    } else {
                        val scaledHeight = height
                        val scaledWidth = scaledHeight * bitmapRatio
                        val left = (width - scaledWidth) / 2
                        RectF(left, 0f, left + scaledWidth, scaledHeight)
                    }
                }
                ScaleType.ORIGINAL -> {
                    val scaledWidth = min(width, bitmap.width.toFloat())
                    val scaledHeight = min(height, bitmap.height.toFloat())
                    val left = (width - scaledWidth) / 2
                    val top = (height - scaledHeight) / 2
                    RectF(left, top, left + scaledWidth, top + scaledHeight)
                }
            }

            canvas.drawBitmap(bitmap, srcRect, dstRect, collagePaint)
            canvas.restore()
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
        cx: Float,
        cy: Float,
        radius: Float,
        s: WallpaperSettings,
        time: Calendar
    ) {
        digitalPaint.color = s.numeralColor
        digitalPaint.textSize = s.numeralSize * 1.5f

        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val timeStr = sdf.format(time.time)

        canvas.drawText(timeStr, cx, cy + (digitalPaint.textSize / 3), digitalPaint)

        // Draw date and day under digital clock
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val textY = cy + (digitalPaint.textSize) + 40f

        if (s.showDate) {
            datePaint.color = s.dateColor
            datePaint.textSize = s.dateSize

            val dateText = dateFormat.format(cal.time)
            canvas.drawText(dateText, cx, textY, datePaint)

            if (s.showDay) {
                datePaint.color = s.dayColor
                datePaint.textSize = s.daySize
                val dayText = dayFormat.format(cal.time)
                canvas.drawText(dayText, cx, textY + s.dateSize + 25f, datePaint)
            }
        } else if (s.showDay) {
            datePaint.color = s.dayColor
            datePaint.textSize = s.daySize
            val dayText = dayFormat.format(cal.time)
            canvas.drawText(dayText, cx, textY, datePaint)
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
