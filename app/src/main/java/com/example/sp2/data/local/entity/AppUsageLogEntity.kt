package com.example.sp2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// One row per calendar date the app was opened
@Entity(tableName = "app_usage_log")
data class AppUsageLogEntity(
    @PrimaryKey val date: String
)