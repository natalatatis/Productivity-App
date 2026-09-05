package com.example.sp2.data.local

import com.example.sp2.data.local.entity.TaskEntity
import com.example.sp2.model.Priority
import com.example.sp2.model.ReminderFrequency
import com.example.sp2.model.RepeatFrequency
import com.example.sp2.model.Task
import java.time.LocalDate
import java.time.LocalTime

// Converts a Task into a database entity
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        date = date?.toString(),
        time = time?.toString(),
        priority = priority.name,
        completed = completed,
        reminder = reminder.name,
        repeat = repeat.name
    )
}

// Converts a database entity into a Task
fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        date = date?.let { LocalDate.parse(it) },
        time = time?.let { LocalTime.parse(it) },
        priority = Priority.valueOf(priority),
        completed = completed,
        reminder = ReminderFrequency.valueOf(reminder),
        repeat = RepeatFrequency.valueOf(repeat)
    )
}