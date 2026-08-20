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
                particles.add(createParticle(w, h, s))
            }
        }

        // Update and draw particles
        for (p in particles) {
            updateParticle(p, w, h, s)
            drawParticle(canvas, p, s)
        }
    }

    private fun createParticle(w: Int, h: Int, s: WallpaperSettings): Particle {
        val r = Random
        val type = s.particleType
        val size = when (type) {
            ParticleType.SNOW -> r.nextFloat() * 8f + 4f
            ParticleType.BUBBLES -> r.nextFloat() * 15f + 5f
            ParticleType.STARS -> r.nextFloat() * 6f + 3f
            ParticleType.FIREFLIES -> r.nextFloat() * 12f + 6f
            ParticleType.RAIN -> r.nextFloat() * 15f + 15f
            ParticleType.CUSTOM -> r.nextFloat() * 16f + 6f
            else -> 5f
        }
        var vx = 0f
        var vy = 0f
        when (type) {
            ParticleType.SNOW -> {
                vx = r.nextFloat() * 1f - 0.5f
                vy = r.nextFloat() * 2f + 1f
            }
            ParticleType.BUBBLES -> {
                vx = r.nextFloat() * 0.8f - 0.4f
                vy = -(r.nextFloat() * 1.5f + 0.5f)
            }
            ParticleType.STARS -> {
                vx = -(r.nextFloat() * 7f + 5f)
                vy = r.nextFloat() * 1.5f + 0.2f
            }
            ParticleType.FIREFLIES -> {
                vx = r.nextFloat() * 1.5f - 0.75f
                vy = r.nextFloat() * 1.0f - 0.5f
            }
            ParticleType.RAIN -> {
                vx = -2f
                vy = r.nextFloat() * 18f + 18f
            }
            ParticleType.CUSTOM -> {
                when (s.customParticleDirection.uppercase()) {
                    "UP" -> {
                        vy = -(r.nextFloat() * 2f + 1f)
                        vx = r.nextFloat() * 0.4f - 0.2f
                    }
                    "DOWN" -> {
                        vy = r.nextFloat() * 2f + 1f
                        vx = r.nextFloat() * 0.4f - 0.2f
                    }
                    "LEFT" -> {
                        vx = -(r.nextFloat() * 2f + 1f)
                        vy = r.nextFloat() * 0.4f - 0.2f
                    }
                    "RIGHT" -> {
                        vx = r.nextFloat() * 2f + 1f
                        vy = r.nextFloat() * 0.4f - 0.2f
                    }
                    "FLOAT" -> {
                        vx = r.nextFloat() * 2f - 1f
                        vy = r.nextFloat() * 2f - 1f
                    }
                    "WAVE" -> {
                        vy = -(r.nextFloat() * 2f + 1f)
                        vx = r.nextFloat() * 0.6f - 0.3f
                    }
                    "EXPLODE" -> {
                        val angle = r.nextFloat() * 2f * Math.PI.toFloat()
                        val speed = r.nextFloat() * 3f + 1f
                        vx = speed * kotlin.math.cos(angle)
                        vy = speed * kotlin.math.sin(angle)
                    }
                }
            }
            else -> {}
        }

        val startX = if (type == ParticleType.CUSTOM && s.customParticleDirection.uppercase() == "EXPLODE") w / 2f else r.nextFloat() * w
        val startY = if (type == ParticleType.CUSTOM && s.customParticleDirection.uppercase() == "EXPLODE") h / 2f else r.nextFloat() * h

        return Particle(
            x = startX,
            y = startY,
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
                    p.y = r.nextFloat() * (h * 0.8f)
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
            ParticleType.CUSTOM -> {
                val dir = s.customParticleDirection.uppercase()
                if (dir == "WAVE") {
                    p.x += kotlin.math.sin(p.y / 30f) * 1.2f
                }
                
                if (dir == "UP" && p.y < -p.size) {
                    p.y = h + p.size
                    p.x = r.nextFloat() * w
                } else if (dir == "DOWN" && p.y > h + p.size) {
                    p.y = -p.size
                    p.x = r.nextFloat() * w
                } else if (dir == "LEFT" && p.x < -p.size) {
                    p.x = w + p.size
                    p.y = r.nextFloat() * h
                } else if (dir == "RIGHT" && p.x > w + p.size) {
                    p.x = -p.size
                    p.y = r.nextFloat() * h
                } else if (dir == "EXPLODE" && (p.x < 0 || p.x > w || p.y < 0 || p.y > h)) {
                    p.x = w / 2f
                    p.y = h / 2f
                    p.alpha = r.nextInt(100) + 100
                } else {
                    if (p.x < -p.size || p.x > w + p.size || p.y < -p.size || p.y > h + p.size) {
                        p.x = r.nextFloat() * w
                        p.y = r.nextFloat() * h
                    }
                }
            }
            else -> {}
        }
    }

    private fun drawParticle(canvas: Canvas, p: Particle, s: WallpaperSettings) {
        paint.alpha = p.alpha
        val type = s.particleType
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
            ParticleType.CUSTOM -> {
                paint.color = s.customParticleColor
                paint.alpha = p.alpha
                
                when (s.customParticleShape.uppercase()) {
                    "CIRCLE" -> {
                        canvas.drawCircle(p.x, p.y, p.size, paint)
                    }
                    "SQUARE" -> {
                        canvas.drawRect(p.x - p.size, p.y - p.size, p.x + p.size, p.y + p.size, paint)
                    }
                    "LINE" -> {
                        paint.strokeWidth = 3f
                        canvas.drawLine(p.x, p.y, p.x + p.vx * 2f, p.y + p.vy * 2f, paint)
                    }
                    "TEXT" -> {
                        paint.textSize = p.size * 1.8f
                        paint.textAlign = Paint.Align.CENTER
                        val glyph = if (s.customParticleText.isNotBlank()) s.customParticleText else "✨"
                        canvas.drawText(glyph, p.x, p.y + p.size * 0.6f, paint)
                    }
                    else -> {
                        canvas.drawCircle(p.x, p.y, p.size, paint)
                    }
                }
            }
            else -> {}
        }
    }
}
