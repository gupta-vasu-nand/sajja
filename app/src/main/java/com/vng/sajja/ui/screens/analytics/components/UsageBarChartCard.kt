package com.vng.sajja.ui.screens.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
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
fun UsageBarChartCard(
    stats: List<DeviceUsageStatsEntity>,
    modifier: Modifier = Modifier
) {
    val displayList = prepare7DayStats(stats)
    val maxMs = displayList.maxOfOrNull { it.screenOnTimeMillis }?.coerceAtLeast(3600000L) ?: (4 * 3600000L)

    val totalWeeklyMs = displayList.sumOf { it.screenOnTimeMillis }
    val activeDays = displayList.count { it.screenOnTimeMillis > 0 }.coerceAtLeast(1)
    val avgWeeklyMs = totalWeeklyMs / activeDays

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary
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
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = "Screen Time",
                        tint = primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Screen Time Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = primaryColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Avg: ${formatMsToHoursMinutes(avgWeeklyMs)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val width = size.width
                val height = size.height - 40.dp.toPx()
                val barCount = displayList.size
                val barSpacing = 12.dp.toPx()
                val totalSpacing = barSpacing * (barCount + 1)
                val barWidth = ((width - totalSpacing) / barCount).coerceAtLeast(16.dp.toPx())

                val paint = android.graphics.Paint().apply {
                    color = textColor.copy(alpha = 0.7f).toArgb()
                    textSize = 10.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }

                val valPaint = android.graphics.Paint().apply {
                    color = primaryColor.toArgb()
                    textSize = 9.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }

                displayList.forEachIndexed { index, item ->
                    val x = barSpacing + index * (barWidth + barSpacing)
                    val rawBarHeight = ((item.screenOnTimeMillis.toFloat() / maxMs) * (height - 20.dp.toPx()))
                    val barHeight = if (item.screenOnTimeMillis > 0) rawBarHeight.coerceAtLeast(8.dp.toPx()) else 4.dp.toPx()
                    val y = height - barHeight

                    val gradient = Brush.verticalGradient(
                        colors = if (item.screenOnTimeMillis > 0) {
                            listOf(primaryColor, secondaryColor)
                        } else {
                            listOf(textColor.copy(alpha = 0.15f), textColor.copy(alpha = 0.05f))
                        }
                    )

                    drawRoundRect(
                        brush = gradient,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )

                    // Draw Value label above non-zero bars
                    if (item.screenOnTimeMillis > 0) {
                        val valText = formatMsToCompact(item.screenOnTimeMillis)
                        drawContext.canvas.nativeCanvas.drawText(
                            valText,
                            x + barWidth / 2f,
                            (y - 4.dp.toPx()).coerceAtLeast(12.dp.toPx()),
                            valPaint
                        )
                    }

                    // Draw Day Label (e.g. Mon, Tue)
                    val dayLabel = try {
                        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(item.dateString)
                        SimpleDateFormat("EEE", Locale.getDefault()).format(parsed ?: Date())
                    } catch (_: Exception) {
                        "Day"
                    }

                    drawContext.canvas.nativeCanvas.drawText(
                        dayLabel,
                        x + barWidth / 2f,
                        size.height - 8.dp.toPx(),
                        paint
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

private fun formatMsToHoursMinutes(ms: Long): String {
    val totalMinutes = ms / (1000 * 60)
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return "${hours}h ${minutes}m"
}

private fun formatMsToCompact(ms: Long): String {
    val totalMinutes = ms / (1000 * 60)
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}
