package com.example.sp2.data

import com.example.sp2.data.local.dao.TaskDao
import com.example.sp2.data.local.dao.TaskListDao
import com.example.sp2.data.local.toEntity
import com.example.sp2.data.local.toTaskList
import com.example.sp2.model.TaskList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskListRepository(
    private val taskListDao: TaskListDao,
    private val taskDao: TaskDao
) {

    val taskLists: Flow<List<TaskList>> =
        taskListDao.getAllLists().map { entities ->
            entities.map {
                it.toTaskList()
            }
        }

    suspend fun getListById(id: Int): TaskList? {
        return taskListDao
            .getListById(id)
            ?.toTaskList()
    }

    // Guarantees a non-deletable "General" folder always exists,
    // reserved at a fixed id — called once when the app opens
    suspend fun ensureGeneralFolder() {
        if (taskListDao.getListById(TaskList.GENERAL_FOLDER_ID) == null) {
            taskListDao.insertList(
                com.example.sp2.data.local.entity.TaskListEntity(
                    id = TaskList.GENERAL_FOLDER_ID,
                    name = "General"
                )
            )
        }
    }

    // Creates a list
    suspend fun addList(name: String): Long {

        val taskList = TaskList(
            name = name
        )

        return taskListDao.insertList(
            taskList.toEntity()
        )
    }

    // Updates a list
    suspend fun updateList(taskList: TaskList) {
        taskListDao.updateList(
            taskList.toEntity()
        )
    }

    // Deletes the tasks inside the list and then the list
    suspend fun deleteList(taskList: TaskList) {

        taskDao.deleteTasksByListId(
            taskList.id
        )

        taskListDao.deleteList(
            taskList.toEntity()
        )
    }
}