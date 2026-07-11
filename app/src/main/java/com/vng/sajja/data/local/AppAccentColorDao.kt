package com.vng.sajja.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppAccentColorDao {
    @Query("SELECT * FROM app_accent_color WHERE id = 1 LIMIT 1")
    fun getAccentColorSync(): AppAccentColorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAccentColorSync(color: AppAccentColorEntity)
}
