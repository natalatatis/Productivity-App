package com.example.sp2.data

import com.example.sp2.model.Priority
import com.example.sp2.model.Task
import java.time.LocalDate
import java.time.LocalTime

// Sample data for first ideas
val sampleTasks = listOf(

    Task(
        id = 1,
        title = "Finish networking homework",
        description = "Complete questions 1–10",
        date = LocalDate.now(),
        time = LocalTime.of(18, 0),
        priority = Priority.HIGH
    ),

    Task(
        id = 2,
        title = "Buy a notebook",
        date = LocalDate.now(),
        priority = Priority.LOW
    ),

    Task(
        id = 3,
        title = "Project meeting",
        date = LocalDate.now().plusDays(1),
        time = LocalTime.of(15, 0),
        priority = Priority.MEDIUM
    ),

    Task(
        id = 4,
        title = "Study for statistics",
        date = LocalDate.now().plusDays(2),
        time = LocalTime.of(10, 0),
        priority = Priority.MEDIUM
    )
)