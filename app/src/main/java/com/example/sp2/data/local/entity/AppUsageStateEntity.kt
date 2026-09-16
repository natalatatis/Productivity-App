package com.example.sp2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// A single row (id is always 1) holding the streak bookkeeping
@Entity(tableName = "app_usage_state")
data class AppUsageStateEntity(
    @PrimaryKey val id: Int = 1,
    val anchorDate: String,
    val streak: Int = 0,
    val consecutiveMisses: Int = 0
)