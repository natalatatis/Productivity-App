package com.example.sp2.model

data class Task(
    val id: Int,
    val title: String,
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val priority: Priority = Priority.MEDIUM,
    val completed: Boolean = false
)