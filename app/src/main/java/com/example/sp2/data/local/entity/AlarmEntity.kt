package com.example.sp2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val hour: Int,
    val minute: Int,
    val label: String = "",
    val days: String = "",
    val enabled: Boolean = true,
    val soundUri: String? = null,
    val snoozeMinutes: Int = 10,
    val specificDate: String? = null
)