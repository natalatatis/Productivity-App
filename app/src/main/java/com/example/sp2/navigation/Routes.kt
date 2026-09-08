package com.example.sp2.navigation

object Routes {

    const val HOME = "home"
    const val TASKS = "tasks"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"

    const val ADD_TASK = "add_task"
    const val ADD_NOTE = "add_note"

    const val MY_STUFF = "my_stuff"

    // Task routes
    const val TASK_DETAIL = "task_detail/{taskId}"

    fun taskDetail(taskId: Int) = "task_detail/$taskId"

    // Note routes
    const val NOTE_DETAIL = "note/{noteId}"

    fun noteDetail(noteId: Int): String {
        return "note/$noteId"
    }

    // Task list routes
    const val TASK_LIST_DETAIL = "task_list/{listId}"

    fun taskListDetail(listId: Int): String {
        return "task_list/$listId"
    }
}