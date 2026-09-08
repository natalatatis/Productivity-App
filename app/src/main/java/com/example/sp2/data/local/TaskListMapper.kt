package com.example.sp2.data.local

import com.example.sp2.data.local.entity.TaskListEntity
import com.example.sp2.model.TaskList

// Converts Room entity to app model
fun TaskListEntity.toTaskList(): TaskList {
    return TaskList(
        id = id,
        name = name
    )
}

// Converts app model to Room entity
fun TaskList.toEntity(): TaskListEntity {
    return TaskListEntity(
        id = id,
        name = name
    )
}