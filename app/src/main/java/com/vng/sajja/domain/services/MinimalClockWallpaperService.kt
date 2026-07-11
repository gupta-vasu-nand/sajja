package com.vng.sajja.domain.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.vng.sajja.SajjaApplication
import com.vng.sajja.domain.model.ClockType
import com.vng.sajja.domain.repository.WallpaperSettingsRepository
import com.vng.sajja.ui.utils.ClockRenderer
import java.util.Calendar

class MinimalClockWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return MinimalClockEngine()
    }

    inner class MinimalClockEngine : Engine() {

        private val handler = Handler(Looper.getMainLooper())
        private var visible = false
        private lateinit var repository: WallpaperSettingsRepository
        private val bitmapCache = mutableMapOf<String, Bitmap>()

        private val ticker = Runnable { drawFrame() }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            repository = (application as SajjaApplication).container.wallpaperSettingsRepository
        }

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
                val settings = repository.load().copy(clockType = ClockType.MINIMALIST)
                val frameDelay = if (settings.smoothSecondHand && settings.clockType != ClockType.DIGITAL) 16L else 1000L

                ClockRenderer.draw(canvas, settings, Calendar.getInstance(), ::loadBitmap)

                surfaceHolder.unlockCanvasAndPost(canvas)

                handler.removeCallbacks(ticker)
                if (visible) handler.postDelayed(ticker, frameDelay)
            } catch (e: Exception) {
                e.printStackTrace()
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
    }
}
