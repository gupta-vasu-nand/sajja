package com.vng.sajja.ui.utils

import android.graphics.*
import com.vng.sajja.domain.model.WallpaperSettings
import kotlin.math.sin
import kotlin.random.Random

/**
 * ChargingEffects - Modular Canvas Charging Animation Renderer.
 * 
 * All canvas charging animations are localized in this single file for easy
 * modification, customization, or style extension.
 */
object ChargingEffects {

    private val auraPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // Particle pool for charging sparks
    private class EnergyParticle(
        var x: Float,
        var y: Float,
        var size: Float,
        var speedY: Float,
        var alpha: Float,
        var waveOffset: Float
    )

    private val particles = MutableList(40) {
        EnergyParticle(
            x = 0f,
            y = 0f,
            size = Random.nextFloat() * 6f + 3f,
            speedY = Random.nextFloat() * 3f + 2f,
            alpha = Random.nextFloat(),
            waveOffset = Random.nextFloat() * 6.28f
        )
    }

    private var lastInitWidth = 0
    private var lastInitHeight = 0

    /**
     * Draws the charging state animation onto the canvas if isCharging is true.
     */
    fun draw(
        canvas: Canvas,
        s: WallpaperSettings,
        isCharging: Boolean
    ) {
        if (!isCharging) return

        val width = canvas.width
        val height = canvas.height
        if (width <= 0 || height <= 0) return

        val time = System.currentTimeMillis()
        val cx = width / 2f
        val cy = height / 2f

        // Initialize particles if canvas dimensions change
        if (width != lastInitWidth || height != lastInitHeight) {
            lastInitWidth = width
            lastInitHeight = height
            particles.forEach { p ->
                p.x = Random.nextFloat() * width
                p.y = Random.nextFloat() * height
            }
        }

        // 1. Draw Ambient Radial Energy Wave from Center
        drawEnergyAura(canvas, cx, cy, width.toFloat(), height.toFloat(), time)

        // 2. Draw Floating Electric Particles
        drawElectricParticles(canvas, width.toFloat(), height.toFloat(), time)

        // 3. Draw Outer Charging Pulse Ring around Clock Face
        drawClockEnergyRing(canvas, cx, cy, minOf(cx, cy) * s.clockSize * 0.65f, time)
    }

    private fun drawEnergyAura(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        width: Float,
        height: Float,
        time: Long
    ) {
        val pulse = (sin(time / 400.0) + 1.0) / 2.0 // 0.0 to 1.0
        val baseRadius = minOf(width, height) * 0.45f + (pulse * 25f).toFloat()

        val colors = intArrayOf(
            Color.argb((90 + pulse * 40).toInt(), 0, 255, 180), // Vibrant Electric Cyan
            Color.argb((35 + pulse * 25).toInt(), 0, 150, 255),  // Deep Neon Blue
            Color.TRANSPARENT
        )
        val stops = floatArrayOf(0f, 0.65f, 1f)

        glowPaint.shader = RadialGradient(
            cx, cy, baseRadius,
            colors, stops,
            Shader.TileMode.CLAMP
        )

        canvas.drawCircle(cx, cy, baseRadius, glowPaint)
        glowPaint.shader = null
    }

    private fun drawElectricParticles(
        canvas: Canvas,
        width: Float,
        height: Float,
        time: Long
    ) {
        particles.forEach { p ->
            p.y -= p.speedY
            p.x += sin(time / 300.0 + p.waveOffset).toFloat() * 1.2f

            if (p.y < 0) {
                p.y = height + Random.nextFloat() * 40f
                p.x = Random.nextFloat() * width
                p.alpha = Random.nextFloat() * 0.8f + 0.2f
            }

            val alphaInt = (p.alpha * 255).toInt().coerceIn(0, 255)
            particlePaint.color = Color.argb(alphaInt, 50, 255, 190)

            canvas.drawCircle(p.x, p.y, p.size, particlePaint)
        }
    }

    private fun drawClockEnergyRing(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        time: Long
    ) {
        if (radius <= 0f) return

        val phase = (time / 25f) % 360f

        auraPaint.strokeWidth = 6f

        auraPaint.shader = SweepGradient(
            cx, cy,
            intArrayOf(
                Color.argb(220, 0, 255, 180),
                Color.argb(70, 0, 150, 255),
                Color.argb(220, 0, 255, 180)
            ),
            floatArrayOf(0f, 0.5f, 1f)
        )

        canvas.save()
        canvas.rotate(phase, cx, cy)
        canvas.drawCircle(cx, cy, radius + 12f, auraPaint)
        canvas.restore()

        auraPaint.shader = null
    }
}
