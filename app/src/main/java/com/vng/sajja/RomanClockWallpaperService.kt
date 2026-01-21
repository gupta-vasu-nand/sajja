package com.vng.sajja

import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.vng.sajja.settings.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class RomanClockWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return RomanClockEngine()
    }

    inner class RomanClockEngine : Engine() {

        private val handler = Handler(Looper.getMainLooper())
        private var visible = false
        private val repository = WallpaperSettingsRepository(this@RomanClockWallpaperService)

        private val bitmapCache = mutableMapOf<String, Bitmap>()

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

        private val ticker = Runnable { drawFrame() }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) handler.post(ticker) else handler.removeCallbacks(ticker)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            visible = false
            handler.removeCallbacks(ticker)
            bitmapCache.values.forEach { it.recycle() }
            bitmapCache.clear()
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            drawFrame()
        }

        private fun drawFrame() {
            val canvas = surfaceHolder.lockCanvas() ?: return
            try {
                val settings = repository.load()

                drawBackground(canvas, settings)

                val cx = canvas.width / 2f
                val cy = canvas.height / 2f
                val baseRadius = min(cx, cy)
                val radius = baseRadius * settings.clockSize * 0.6f

                val frameDelay = if (settings.smoothSecondHand) 16L else 1000L

                if (settings.showBorder) {
                    borderPaint.color = settings.borderColor
                    borderPaint.strokeWidth = settings.borderWidth
                    canvas.drawCircle(cx, cy, radius, borderPaint)
                }

                if (settings.showNumerals) {
                    drawNumerals(canvas, cx, cy, radius, settings)
                }

                drawHands(canvas, cx, cy, radius, settings)
                drawCenterKnob(canvas, cx, cy, settings)
                drawDate(canvas, cx, cy, radius, settings)

                surfaceHolder.unlockCanvasAndPost(canvas)

                handler.removeCallbacks(ticker)
                if (visible) handler.postDelayed(ticker, frameDelay)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun drawBackground(canvas: Canvas, s: WallpaperSettings) {
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
                    canvas.drawRect(
                        0f, 0f,
                        canvas.width.toFloat(),
                        canvas.height.toFloat(),
                        bgPaint
                    )
                    bgPaint.shader = null
                }
                BackgroundType.COLLAGE -> {
                    canvas.drawColor(s.backgroundColor)
                    drawCollage(canvas, s)
                }
            }
        }

        private fun drawCollage(canvas: Canvas, settings: WallpaperSettings) {
            val collageImages = settings.collageImages.sortedBy { it.zIndex }

            collagePaint.alpha = (settings.collageOpacity * 255).toInt()

            for (image in collageImages) {
                val bitmap = loadBitmap(image.uri) ?: continue

                collagePaint.alpha = (image.opacity * settings.collageOpacity * 255).toInt()

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

        private fun loadBitmap(uriString: String): Bitmap? {
            bitmapCache[uriString]?.let { return it }

            return try {
                val uri = android.net.Uri.parse(uriString)
                val inputStream = contentResolver.openInputStream(uri)
                val options = BitmapFactory.Options().apply {
                    inSampleSize = 2
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
                inputStream?.close()

                if (bitmap != null) {
                    bitmapCache[uriString] = bitmap
                }
                bitmap
            } catch (_: Exception) {
                null
            }
        }

        private fun drawNumerals(
            canvas: Canvas,
            cx: Float,
            cy: Float,
            radius: Float,
            s: WallpaperSettings
        ) {
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

        private fun drawHands(
            canvas: Canvas,
            cx: Float,
            cy: Float,
            radius: Float,
            s: WallpaperSettings
        ) {
            val cal = Calendar.getInstance()

            val sec = cal.get(Calendar.SECOND) + cal.get(Calendar.MILLISECOND) / 1000f
            val min = cal.get(Calendar.MINUTE)
            val hr = cal.get(Calendar.HOUR) % 12

            hourPaint.color = s.hourHandColor
            hourPaint.strokeWidth = s.hourHandWidth

            minutePaint.color = s.minuteHandColor
            minutePaint.strokeWidth = s.minuteHandWidth

            secondPaint.color = s.secondHandColor
            secondPaint.strokeWidth = s.secondHandWidth

            val borderPadding = if (s.showBorder) s.borderWidth / 2 else 0f
            val innerRadius = radius - borderPadding - (s.numeralSize / 3)

            val hourAngle = Math.toRadians(((hr + min / 60f) * 30 - 90).toDouble())
            val minAngle = Math.toRadians((min * 6 - 90).toDouble())
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

        private fun drawDate(canvas: Canvas, cx: Float, cy: Float, radius: Float, s: WallpaperSettings) {
            val cal = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

            val textY = cy + radius + 60f

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
    }
}