package com.example.sp2.model

import java.time.DayOfWeek
import java.time.LocalTime

// days empty = one-time alarm (not repeating)
data class Alarm(
    val id: Int = 0,
    val time: LocalTime,
    val label: String = "",
    val days: Set<DayOfWeek> = emptySet(),
    val enabled: Boolean = true,
    val soundUri: String? = null,
    val snoozeMinutes: Int = 10
)