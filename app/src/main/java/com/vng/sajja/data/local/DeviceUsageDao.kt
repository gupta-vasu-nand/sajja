package com.vng.sajja.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceUsageDao {
    @Query("SELECT * FROM device_usage_stats ORDER BY timestamp DESC LIMIT 7")
    fun getPastWeekStats(): Flow<List<DeviceUsageStatsEntity>>

    @Query("SELECT * FROM device_usage_stats WHERE dateString = :date LIMIT 1")
    fun getStatsForDate(date: String): DeviceUsageStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertStats(stats: DeviceUsageStatsEntity)

    @Query("SELECT * FROM device_usage_stats ORDER BY timestamp ASC")
    fun getAllStats(): Flow<List<DeviceUsageStatsEntity>>
}
