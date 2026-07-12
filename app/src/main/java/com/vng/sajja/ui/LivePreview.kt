package com.vng.sajja.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.utils.ClockRenderer
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds
import androidx.core.net.toUri

@Composable
fun LivePreview(
    settings: WallpaperSettings,
    isVisible: Boolean = true,
    modifier: Modifier = Modifier
) {
    var tick by remember { mutableStateOf(System.currentTimeMillis()) }
    val context = LocalContext.current
    val bitmapCache = remember { mutableMapOf<String, Bitmap>() }

    val isAnimated = isVisible && (settings.smoothSecondHand ||
            settings.particleType != com.vng.sajja.domain.model.ParticleType.NONE ||
            settings.digitalAnimType != com.vng.sajja.domain.model.DigitalAnimType.NONE)

    LaunchedEffect(isAnimated) {
        // Run timing tick on a background thread dispatcher to offload UI thread
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
            ClockRenderer.draw(nativeCanvas, settings, time, loadBitmap)
        }
    }
}