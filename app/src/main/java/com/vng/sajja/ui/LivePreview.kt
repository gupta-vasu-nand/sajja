package com.vng.sajja.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.utils.ClockRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.vng.sajja.ui.theme.getContrastingTextColor

@Composable
fun LivePreview(
    settings: WallpaperSettings,
    isVisible: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showFullscreenDialog by remember { mutableStateOf(false) }
    var isDeviceCharging by remember { mutableStateOf(false) }
    var isSimulatedCharging by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Listen to real device battery charging status
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    isDeviceCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val stickyIntent = context.registerReceiver(receiver, filter)
        if (stickyIntent != null) {
            val status = stickyIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            isDeviceCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
        }
        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {}
        }
    }

    val activeChargingState = isDeviceCharging || isSimulatedCharging

    val isDark = MaterialTheme.colorScheme.background == Color.Black

    val cardBgColor = if (isDark) {
        Color.White.copy(alpha = 0.05f)
    } else {
        Color.Black.copy(alpha = 0.03f)
    }

    val cardBorderColor = if (isDark) {
        Color.White.copy(alpha = 0.12f)
    } else {
        Color.Black.copy(alpha = 0.08f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardBgColor)
            .border(1.dp, cardBorderColor, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: Phone Device Mockup preserving 9:16 mobile aspect ratio
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(9f / 16f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .border(
                        border = BorderStroke(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { showFullscreenDialog = true }
            ) {
                RawLivePreviewCanvas(
                    settings = settings,
                    isVisible = isVisible,
                    isCharging = activeChargingState,
                    modifier = Modifier.fillMaxSize()
                )

                // Phone Camera Punch Hole Notch Mockup
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .width(26.dp)
                        .height(5.dp)
                        .align(Alignment.TopCenter)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.9f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                )
            }

            // RIGHT: Reorganized Glassmorphic Control Panel
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 2.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Top Right Live Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    val statusColor = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF6750A4)
                    Surface(
                        shape = CircleShape,
                        color = statusColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                            )
                            Text(
                                text = "60 FPS • Live",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = statusColor
                            )
                        }
                    }
                }

                // 2. Center: Clock Style Heading & Background Subtitle
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${settings.clockType.name} STYLE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "Background: ${settings.backgroundType.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // 3. Bottom Row: Side-by-Side Test Charging & Fullscreen Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Test Charging Button (Left)
                    val chipBg = if (activeChargingState) {
                        Color(0xFF00E676).copy(alpha = 0.18f)
                    } else {
                        if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)
                    }
                    val chipBorder = if (activeChargingState) Color(0xFF00E676) else (if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.12f))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = chipBg,
                        border = BorderStroke(1.dp, chipBorder),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clickable { isSimulatedCharging = !isSimulatedCharging }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (activeChargingState) "⚡ Charging" else "⚡ Charging",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (activeChargingState) Color(0xFF00C853) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Fullscreen Action Button (Right)
                    val primaryBtnBg = MaterialTheme.colorScheme.primary
                    val primaryBtnFg = getContrastingTextColor(primaryBtnBg)

                    Button(
                        onClick = { showFullscreenDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBtnBg,
                            contentColor = primaryBtnFg
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = "Expand Fullscreen",
                            tint = primaryBtnFg,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Fullscreen",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = primaryBtnFg
                        )
                    }
                }
            }
        }
    }

    // Fullscreen Glassmorphic Interactive Preview Dialog
    if (showFullscreenDialog) {
        Dialog(
            onDismissRequest = { showFullscreenDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { showFullscreenDialog = false }
            ) {
                // Fullscreen Live Canvas
                RawLivePreviewCanvas(
                    settings = settings,
                    isVisible = true,
                    isCharging = activeChargingState,
                    modifier = Modifier.fillMaxSize()
                )

                // Glassmorphic Top Overlay Bar
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Glassmorphic Close Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .clickable { showFullscreenDialog = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close preview",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Glassmorphic Test Charging Toggle
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (activeChargingState) Color(0xFF00E676).copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, if (activeChargingState) Color(0xFF00E676) else Color.White.copy(alpha = 0.25f)),
                        modifier = Modifier.clickable { isSimulatedCharging = !isSimulatedCharging }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (activeChargingState) "⚡ Charging ON" else "⚡ Test Charging",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (activeChargingState) Color(0xFF00E676) else Color.White
                            )
                        }
                    }

                    // Glassmorphic Status Indicator Badge
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.Green)
                            )
                            Text(
                                text = "60 FPS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Glassmorphic Bottom Hint Chip
                Surface(
                    shape = RoundedCornerShape(30.dp),
                    color = Color.Black.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 24.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = "Tap anywhere to exit preview",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RawLivePreviewCanvas(
    settings: WallpaperSettings,
    isVisible: Boolean,
    isCharging: Boolean = false,
    modifier: Modifier = Modifier
) {
    var tick by remember { mutableStateOf(System.currentTimeMillis()) }
    val context = LocalContext.current
    val bitmapCache = remember { mutableMapOf<String, Bitmap>() }

    val isAnimated = isVisible && (isCharging || settings.smoothSecondHand ||
            settings.particleType != com.vng.sajja.domain.model.ParticleType.NONE ||
            settings.digitalAnimType != com.vng.sajja.domain.model.DigitalAnimType.NONE)

    LaunchedEffect(isAnimated) {
        withContext(Dispatchers.Default) {
            while (true) {
                tick = System.currentTimeMillis()
                delay((if (isAnimated) 16 else 1000).milliseconds)
            }
        }
    }

    val time = remember { Calendar.getInstance() }
    time.timeInMillis = tick

    DisposableEffect(Unit) {
        onDispose {
            bitmapCache.values.forEach { it.recycle() }
            bitmapCache.clear()
        }
    }

    val loadBitmap = remember(context) {
        { uriString: String ->
            bitmapCache[uriString] ?: try {
                val uri = uriString.toUri()
                val inputStream = context.contentResolver.openInputStream(uri)
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
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        drawIntoCanvas { composeCanvas ->
            val nativeCanvas = composeCanvas.nativeCanvas
            ClockRenderer.draw(nativeCanvas, settings, time, loadBitmap, isCharging)
        }
    }
}
