package com.example.sp2.navigation

object Routes {

    const val HOME = "home"
    const val TASKS = "tasks"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"

    const val ADD_TASK = "add_task"
    const val ADD_NOTE = "add_note?listId={listId}"

    fun addNote(listId: Int? = null): String {
        return if (listId != null) {
            "add_note?listId=$listId"
        } else {
            "add_note"
        }
    }

    const val MY_STUFF = "my_stuff"
    const val REMINDERS = "reminders"

    // Task routes
    const val TASK_DETAIL = "task_detail/{taskId}"

    fun taskDetail(taskId: Int) = "task_detail/$taskId"

    // Note routes
    const val NOTE_DETAIL = "note/{noteId}"

    fun noteDetail(noteId: Int): String {
        return "note/$noteId"
    }

    const val TASK_FOLDERS = "task_folders"
    const val NOTE_FOLDERS = "note_folders"
    const val NOTE_FOLDER_DETAIL = "note_folder/{listId}"

    fun noteFolderDetail(listId: Int): String {
        return "note_folder/$listId"
    }

    // Task list routes
    const val TASK_LIST_DETAIL = "task_list/{listId}"

    fun taskListDetail(listId: Int): String {
        return "task_list/$listId"
    }
}