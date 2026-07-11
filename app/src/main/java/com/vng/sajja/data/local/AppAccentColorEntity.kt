package com.vng.sajja.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_accent_color")
data class AppAccentColorEntity(
    @PrimaryKey val id: Int = 1,
    val colorHex: String?
)
