package com.example.sp2.model

// weekUsage: Monday to Sunday of the current week.
// null = that day hasn't happened yet, true = opened the app,
// false = day already passed without opening it
data class AppUsage(
    val streak: Int = 0,
    val totalDaysUsed: Int = 0,
    val weekUsage: List<Boolean?> = List(7) { null }
)