package com.vng.sajja.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_animations")
data class CustomAnimationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val particleType: String,
    val particleSpeed: Float,
    val particleCount: Int,
    val digitalAnimType: String,
    val digitalAnimDuration: Long,
    val customParticleShape: String = "CIRCLE",
    val customParticleText: String = "✨",
    val customParticleDirection: String = "UP",
    val customParticleColor: Int = -1 // Color.WHITE is represented as -1 (0xFFFFFFFF)
)
