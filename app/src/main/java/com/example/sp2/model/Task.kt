package com.example.sp2.model

import java.time.LocalDate
import java.time.LocalTime

data class Task(
    val id: Int,
    val title: String,
    val description: String = "",
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val priority: Priority = Priority.MEDIUM,
    val completed: Boolean = false,
    val reminder: ReminderFrequency = ReminderFrequency.NONE,
    val repeat: RepeatFrequency = RepeatFrequency.NONE
)