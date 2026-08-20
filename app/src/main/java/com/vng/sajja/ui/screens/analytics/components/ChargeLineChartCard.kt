package com.vng.sajja.ui.screens.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vng.sajja.data.local.DeviceUsageStatsEntity
import com.vng.sajja.ui.components.GlassmorphicCard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ChargeLineChartCard(
    stats: List<DeviceUsageStatsEntity>,
    modifier: Modifier = Modifier
) {
    val displayList = prepare7DayStats(stats)
    val maxCharges = displayList.maxOfOrNull { it.chargeCount }?.coerceAtLeast(3) ?: 3
    val totalCharges = displayList.sumOf { it.chargeCount }

    val accentGreen = Color(0xFF00E676)
    val textColor = MaterialTheme.colorScheme.onSurface

    GlassmorphicCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Charge Cycles",
                        tint = accentGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Charge Frequency",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accentGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Total: $totalCharges charges",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = accentGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                if (displayList.isEmpty()) return@Canvas

                val width = size.width
                val height = size.height - 30.dp.toPx()
                val stepX = width / (displayList.size - 1).coerceAtLeast(1)

                val points = displayList.mapIndexed { index, item ->
                    val x = index * stepX
                    val y = height - ((item.chargeCount.toFloat() / maxCharges) * (height - 30.dp.toPx()))
                    Offset(x, y)
                }

                val strokePath = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val controlPoint1 = Offset(p1.x + (p2.x - p1.x) / 2f, p1.y)
                            val controlPoint2 = Offset(p1.x + (p2.x - p1.x) / 2f, p2.y)
                            cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y)
                        }
                    }
                }

                val fillPath = Path().apply {
                    addPath(strokePath)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                // Draw Gradient Fill under line
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(accentGreen.copy(alpha = 0.35f), Color.Transparent)
                    )
                )

                // Draw Line Path
                drawPath(
                    path = strokePath,
                    color = accentGreen,
                    style = Stroke(width = 3.dp.toPx())
                )

                val labelPaint = android.graphics.Paint().apply {
                    color = textColor.copy(alpha = 0.7f).toArgb()
                    textSize = 10.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }

                val valPaint = android.graphics.Paint().apply {
                    color = accentGreen.toArgb()
                    textSize = 9.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }

                // Draw Nodes & Day Labels
                points.forEachIndexed { index, pt ->
                    drawCircle(
                        color = Color.Black,
                        radius = 6.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = accentGreen,
                        radius = 4.dp.toPx(),
                        center = pt
                    )

                    val valText = "${displayList[index].chargeCount}x"
                    drawContext.canvas.nativeCanvas.drawText(
                        valText,
                        pt.x,
                        pt.y - 10.dp.toPx(),
                        valPaint
                    )

                    // Draw Day Label below
                    val dayLabel = try {
                        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(displayList[index].dateString)
                        SimpleDateFormat("EEE", Locale.getDefault()).format(parsed ?: Date())
                    } catch (_: Exception) {
                        "Day"
                    }

                    drawContext.canvas.nativeCanvas.drawText(
                        dayLabel,
                        pt.x,
                        size.height - 4.dp.toPx(),
                        labelPaint
                    )
                }
            }
        }
    }
}

private fun prepare7DayStats(stats: List<DeviceUsageStatsEntity>): List<DeviceUsageStatsEntity> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val map = stats.associateBy { it.dateString }

    val list = mutableListOf<DeviceUsageStatsEntity>()
    for (i in 6 downTo 0) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -i)
        val dateStr = dateFormat.format(cal.time)

        val found = map[dateStr] ?: DeviceUsageStatsEntity(
            dateString = dateStr,
            timestamp = cal.timeInMillis,
            chargeCount = 0,
            screenOnTimeMillis = 0L,
            screenOffTimeMillis = 0L,
            lastBatteryLevel = 100
        )
        list.add(found)
    }
    return list
}
