package com.vng.sajja.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.vng.sajja.settings.BackgroundType
import com.vng.sajja.settings.WallpaperSettings
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun LivePreview(settings: WallpaperSettings, modifier: Modifier = Modifier) {
    var time by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(settings.smoothSecondHand) {
        while (true) {
            time = Calendar.getInstance()
            delay(if (settings.smoothSecondHand) 16 else 1000)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        when (settings.backgroundType) {
            BackgroundType.SOLID -> {
                drawRect(Color(settings.backgroundColor))
            }
            BackgroundType.GRADIENT -> {
                drawRect(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(settings.gradientStartColor),
                            Color(settings.gradientEndColor)
                        )
                    )
                )
            }
            BackgroundType.COLLAGE -> {
                drawRect(Color(settings.backgroundColor))
                drawRect(
                    Color.Black.copy(alpha = settings.collageOpacity * 0.3f)
                )
            }
        }

        val cx = size.width / 2
        val cy = size.height / 2
        val baseRadius = min(cx, cy)
        val clockRadius = baseRadius * settings.clockSize * 0.6f
        val borderPadding = if (settings.showBorder) settings.borderWidth / 2 else 0f
        val innerRadius = clockRadius - borderPadding - (settings.numeralSize / 3)

        if (settings.showBorder) {
            drawCircle(
                color = Color(settings.borderColor),
                radius = clockRadius,
                style = Stroke(width = settings.borderWidth)
            )
        }

        if (settings.showNumerals) {
            val romans = arrayOf(
                "XII", "I", "II", "III", "IV", "V",
                "VI", "VII", "VIII", "IX", "X", "XI"
            )
            val paint = android.graphics.Paint().apply {
                color = settings.numeralColor
                textSize = settings.numeralSize
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }

            for (i in romans.indices) {
                val angle = Math.toRadians((i * 30 - 90).toDouble())
                val x = cx + cos(angle) * innerRadius * 0.9
                val y = cy + sin(angle) * innerRadius * 0.9 + settings.numeralSize / 3
                drawContext.canvas.nativeCanvas.drawText(
                    romans[i],
                    x.toFloat(),
                    y.toFloat(),
                    paint
                )
            }
        }

        val sec = time.get(Calendar.SECOND) +
                time.get(Calendar.MILLISECOND) / 1000f
        val min = time.get(Calendar.MINUTE)
        val hr = time.get(Calendar.HOUR) % 12

        val hourAngle = Math.toRadians(((hr + min / 60f) * 30 - 90).toDouble())
        val minAngle = Math.toRadians((min * 6 - 90).toDouble())
        val secAngle = Math.toRadians(sec * 6.0 - 90)

        drawLine(
            color = Color(settings.hourHandColor),
            start = Offset(cx, cy),
            end = Offset(
                (cx + cos(hourAngle) * innerRadius * 0.5).toFloat(),
                (cy + sin(hourAngle) * innerRadius * 0.5).toFloat()
            ),
            strokeWidth = settings.hourHandWidth
        )

        drawLine(
            color = Color(settings.minuteHandColor),
            start = Offset(cx, cy),
            end = Offset(
                (cx + cos(minAngle) * innerRadius * 0.7).toFloat(),
                (cy + sin(minAngle) * innerRadius * 0.7).toFloat()
            ),
            strokeWidth = settings.minuteHandWidth
        )

        if (settings.showSecondHand) {
            drawLine(
                color = Color(settings.secondHandColor),
                start = Offset(cx, cy),
                end = Offset(
                    (cx + cos(secAngle) * innerRadius * 0.85).toFloat(),
                    (cy + sin(secAngle) * innerRadius * 0.85).toFloat()
                ),
                strokeWidth = settings.secondHandWidth
            )
        }

        drawCircle(
            color = Color(settings.centerRingColor),
            radius = settings.centerKnobRadius + settings.centerRingWidth,
            style = Stroke(width = settings.centerRingWidth)
        )

        drawCircle(
            color = Color(settings.centerRingColor),
            radius = settings.centerKnobRadius - settings.centerRingWidth / 2,
            style = Stroke(width = settings.centerRingWidth / 2)
        )

        drawCircle(
            color = Color(settings.centerKnobColor),
            radius = settings.centerKnobRadius,
            center = Offset(cx, cy)
        )

        val date = Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        val dayFormat = java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault())

        val textPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }

        val textY = cy + clockRadius + 60f

        if (settings.showDate) {
            textPaint.color = settings.dateColor
            textPaint.textSize = settings.dateSize
            val dateText = dateFormat.format(date.time)
            drawContext.canvas.nativeCanvas.drawText(
                dateText,
                cx,
                textY,
                textPaint
            )

            if (settings.showDay) {
                textPaint.color = settings.dayColor
                textPaint.textSize = settings.daySize
                val dayText = dayFormat.format(date.time)
                drawContext.canvas.nativeCanvas.drawText(
                    dayText,
                    cx,
                    textY + settings.dateSize + 25f,
                    textPaint
                )
            }
        } else if (settings.showDay) {
            textPaint.color = settings.dayColor
            textPaint.textSize = settings.daySize
            val dayText = dayFormat.format(date.time)
            drawContext.canvas.nativeCanvas.drawText(
                dayText,
                cx,
                textY,
                textPaint
            )
        }
    }
}