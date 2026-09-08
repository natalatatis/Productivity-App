package com.example.sp2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room table for task lists
@Entity(tableName = "task_lists")
data class TaskListEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String
)