package com.vng.sajja.ui.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.vng.sajja.domain.model.ParticleType
import com.vng.sajja.domain.model.WallpaperSettings
import kotlin.random.Random

class Particle(
    var x: Float,
    var y: Float,
    var size: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Int,
    var alphaDir: Int = 1
)

object ParticleEffects {
    private val particles = mutableListOf<Particle>()
    private var lastWidth = 0
    private var lastHeight = 0
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun draw(canvas: Canvas, s: WallpaperSettings) {
        if (s.particleType == ParticleType.NONE) return

        val w = canvas.width
        val h = canvas.height

        // Re-initialize particles if screen size changes or particle count list is empty
        if (w != lastWidth || h != lastHeight || particles.isEmpty() || particles.size != s.particleCount) {
            lastWidth = w
            lastHeight = h
            particles.clear()
            for (i in 0 until s.particleCount) {
                particles.add(createParticle(w, h, s.particleType))
            }
        }

        // Update and draw particles
        for (p in particles) {
            updateParticle(p, w, h, s)
            drawParticle(canvas, p, s.particleType)
        }
    }

    private fun createParticle(w: Int, h: Int, type: ParticleType): Particle {
        val r = Random
        val size = when (type) {
            ParticleType.SNOW -> r.nextFloat() * 8f + 4f
            ParticleType.BUBBLES -> r.nextFloat() * 15f + 5f
            ParticleType.STARS -> r.nextFloat() * 6f + 3f
            ParticleType.FIREFLIES -> r.nextFloat() * 12f + 6f
            ParticleType.RAIN -> r.nextFloat() * 15f + 15f
            else -> 5f
        }
        val vx = when (type) {
            ParticleType.SNOW -> r.nextFloat() * 1f - 0.5f
            ParticleType.BUBBLES -> r.nextFloat() * 0.8f - 0.4f
            ParticleType.STARS -> -(r.nextFloat() * 7f + 5f)
            ParticleType.FIREFLIES -> r.nextFloat() * 1.5f - 0.75f
            ParticleType.RAIN -> -2f
            else -> 0f
        }
        val vy = when (type) {
            ParticleType.SNOW -> r.nextFloat() * 2f + 1f
            ParticleType.BUBBLES -> -(r.nextFloat() * 1.5f + 0.5f)
            ParticleType.STARS -> r.nextFloat() * 1.5f + 0.2f
            ParticleType.FIREFLIES -> r.nextFloat() * 1.0f - 0.5f
            ParticleType.RAIN -> r.nextFloat() * 18f + 18f
            else -> 0f
        }
        return Particle(
            x = r.nextFloat() * w,
            y = r.nextFloat() * h,
            size = size,
            vx = vx,
            vy = vy,
            alpha = r.nextInt(100) + 100
        )
    }

    private fun updateParticle(p: Particle, w: Int, h: Int, s: WallpaperSettings) {
        val speedMultiplier = s.particleSpeed
        p.x += p.vx * speedMultiplier
        p.y += p.vy * speedMultiplier

        val r = Random
        when (s.particleType) {
            ParticleType.SNOW -> {
                p.vx += (r.nextFloat() * 0.2f - 0.1f)
                p.vx = p.vx.coerceIn(-1.5f, 1.5f)
                
                if (p.y > h) {
                    p.y = -p.size
                    p.x = r.nextFloat() * w
                }
                if (p.x < 0 || p.x > w) {
                    p.x = r.nextFloat() * w
                }
            }
            ParticleType.BUBBLES -> {
                p.vx += (r.nextFloat() * 0.1f - 0.05f)
                p.vx = p.vx.coerceIn(-1f, 1f)

                if (p.y < -p.size) {
                    p.y = h + p.size
                    p.x = r.nextFloat() * w
                }
            }
            ParticleType.STARS -> {
                p.alpha += p.alphaDir * 4
                if (p.alpha > 230) {
                    p.alpha = 230
                    p.alphaDir = -1
                } else if (p.alpha < 50) {
                    p.alpha = 50
                    p.alphaDir = 1
                }
                if (p.x < -p.size * 6f || p.y > h + p.size) {
                    p.x = w + p.size
                    p.y = r.nextFloat() * (h * 0.8f) // Spawn mostly in upper 80%
                    p.alpha = r.nextInt(100) + 100
                }
            }
            ParticleType.FIREFLIES -> {
                p.vx += (r.nextFloat() * 0.4f - 0.2f)
                p.vy += (r.nextFloat() * 0.4f - 0.2f)
                p.vx = p.vx.coerceIn(-2f, 2f)
                p.vy = p.vy.coerceIn(-2f, 2f)

                p.alpha += p.alphaDir * 5
                if (p.alpha > 240) {
                    p.alpha = 240
                    p.alphaDir = -1
                } else if (p.alpha < 30) {
                    p.alpha = 30
                    p.alphaDir = 1
                }

                if (p.x < 0 || p.x > w) p.x = r.nextFloat() * w
                if (p.y < 0 || p.y > h) p.y = r.nextFloat() * h
            }
            ParticleType.RAIN -> {
                if (p.y > h) {
                    p.y = -p.size
                    p.x = r.nextFloat() * w
                }
            }
            else -> {}
        }
    }

    private fun drawParticle(canvas: Canvas, p: Particle, type: ParticleType) {
        paint.alpha = p.alpha
        when (type) {
            ParticleType.SNOW -> {
                paint.color = Color.WHITE
                canvas.drawCircle(p.x, p.y, p.size, paint)
            }
            ParticleType.BUBBLES -> {
                paint.color = Color.WHITE
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 2f
                canvas.drawCircle(p.x, p.y, p.size, paint)
                paint.style = Paint.Style.FILL
            }
            ParticleType.STARS -> {
                paint.color = Color.parseColor("#FFFDD0")
                paint.strokeWidth = 3f
                paint.alpha = p.alpha / 3
                canvas.drawLine(p.x, p.y, p.x - p.vx * 3f, p.y - p.vy * 3f, paint)

                paint.alpha = p.alpha
                canvas.drawCircle(p.x, p.y, p.size * 0.4f, paint)
            }
            ParticleType.FIREFLIES -> {
                paint.color = Color.parseColor("#ADFF2F")
                canvas.drawCircle(p.x, p.y, p.size, paint)
                paint.alpha = p.alpha / 3
                canvas.drawCircle(p.x, p.y, p.size * 2.2f, paint)
            }
            ParticleType.RAIN -> {
                paint.color = Color.parseColor("#80A0C0")
                paint.strokeWidth = 3f
                canvas.drawLine(p.x, p.y, p.x + p.vx, p.y + p.size, paint)
            }
            else -> {}
        }
    }
}
