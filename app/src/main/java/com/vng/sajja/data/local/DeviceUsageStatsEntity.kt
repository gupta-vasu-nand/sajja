package com.vng.sajja.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_usage_stats")
data class DeviceUsageStatsEntity(
    @PrimaryKey
    val dateString: String, // Format: "yyyy-MM-dd" e.g., "2026-08-21"
    val timestamp: Long = System.currentTimeMillis(),
    val chargeCount: Int = 0,
    val screenOnTimeMillis: Long = 0L,
    val screenOffTimeMillis: Long = 0L,
    val lastBatteryLevel: Int = 100
)
