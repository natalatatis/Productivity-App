package com.example.sp2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sp2.data.local.entity.AppUsageLogEntity
import com.example.sp2.data.local.entity.AppUsageStateEntity

@Dao
interface AppUsageDao {

    @Query("SELECT * FROM app_usage_state WHERE id = 1 LIMIT 1")
    suspend fun getState(): AppUsageStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertState(state: AppUsageStateEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun logDay(entry: AppUsageLogEntity)

    @Query("SELECT COUNT(*) FROM app_usage_log")
    suspend fun getTotalDaysUsed(): Int

    @Query("SELECT date FROM app_usage_log WHERE date IN (:dates)")
    suspend fun getUsedDates(dates: List<String>): List<String>
}