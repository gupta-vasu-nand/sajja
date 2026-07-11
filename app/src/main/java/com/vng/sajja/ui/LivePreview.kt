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
import java.util.Calendar

@Composable
fun LivePreview(settings: WallpaperSettings, modifier: Modifier = Modifier) {
    var time by remember { mutableStateOf(Calendar.getInstance()) }
    val context = LocalContext.current
    val bitmapCache = remember { mutableMapOf<String, Bitmap>() }

    LaunchedEffect(settings.smoothSecondHand) {
        while (true) {
            time = Calendar.getInstance()
            delay(if (settings.smoothSecondHand) 16 else 1000)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            bitmapCache.values.forEach { it.recycle() }
            bitmapCache.clear()
        }
    }

    val loadBitmap = remember(context) {
        { uriString: String ->
            bitmapCache[uriString] ?: try {
                val uri = android.net.Uri.parse(uriString)
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