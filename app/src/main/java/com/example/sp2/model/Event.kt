package com.example.sp2.model

import java.time.LocalDate
import java.time.LocalTime

data class Event(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val date: LocalDate,
    val time: LocalTime? = null
)