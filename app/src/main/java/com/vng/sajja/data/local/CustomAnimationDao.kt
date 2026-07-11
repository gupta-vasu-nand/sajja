package com.vng.sajja.data.local

import androidx.room.*

@Dao
interface CustomAnimationDao {
    @Query("SELECT * FROM custom_animations ORDER BY name ASC")
    fun getAllAnimations(): List<CustomAnimationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnimation(animation: CustomAnimationEntity)

    @Update
    fun updateAnimation(animation: CustomAnimationEntity)

    @Delete
    fun deleteAnimation(animation: CustomAnimationEntity)
}
