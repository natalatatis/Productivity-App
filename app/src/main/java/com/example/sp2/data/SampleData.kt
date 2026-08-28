package com.example.sp2.data

import com.example.sp2.model.Priority
import com.example.sp2.model.Task
//Sample data for first ideas
val sampleTasks = listOf(

    Task(
        id = 1,
        title = "Finish networking homework",
        description = "Complete questions 1–10",
        date = "Today",
        time = "6:00 PM",
        priority = Priority.HIGH
    ),

    Task(
        id = 2,
        title = "Buy a notebook",
        date = "Today",
        time = "Anytime",
        priority = Priority.LOW
    ),

    Task(
        id = 3,
        title = "Project meeting",
        date = "Tomorrow",
        time = "3:00 PM",
        priority = Priority.MEDIUM
    ),

    Task(
        id = 4,
        title = "Study for statistics",
        date = "Saturday",
        time = "10:00 AM",
        priority = Priority.MEDIUM
    )
)