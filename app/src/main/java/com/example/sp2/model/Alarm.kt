package com.example.sp2.model

import java.time.DayOfWeek
import java.time.LocalTime
import java.time.LocalDate

// days empty = one-time alarm (not repeating)
data class Alarm(
    val id: Int = 0,
    val time: LocalTime,
    val label: String = "",
    val days: Set<DayOfWeek> = emptySet(),
    val enabled: Boolean = true,
    val soundUri: String? = null,
    val snoozeMinutes: Int = 10,
    // When set (and days is empty), this alarm rings on this exact
    // date instead of "the next time this clock time comes around"
    val specificDate: LocalDate? = null
)