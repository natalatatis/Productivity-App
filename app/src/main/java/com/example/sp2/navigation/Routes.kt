package com.example.sp2.navigation

object Routes {

    const val HOME = "home"
    const val TASKS = "tasks"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"

    const val ADD_TASK = "add_task"
    const val ADD_NOTE = "add_note"

    // Route pattern used by the NavHost (includes the argument placeholder)
    const val TASK_DETAIL = "task_detail/{taskId}"

    // Builds the actual navigable route for a specific task
    fun taskDetail(taskId: Int) = "task_detail/$taskId"
}