package com.example.sp2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val type: String,
    val frequency: String,
    val targetCount: Int,

    // The date this habit's current-period progress is anchored to
    val anchorDate: String,
    val currentCount: Int = 0,

    val streak: Int = 0,
    val consecutiveMisses: Int = 0,

    val durationType: String = "INDEFINITE",
    val totalPeriods: Int? = null,

    val totalHits: Int = 0,
    val totalMisses: Int = 0
)