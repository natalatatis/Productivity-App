package com.example.sp2.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sp2.ui.components.AppBottomBar
import com.example.sp2.ui.screens.calendar.CalendarScreen
import com.example.sp2.ui.screens.home.HomeScreen
import com.example.sp2.ui.screens.mystuff.MyStuffScreen
import com.example.sp2.ui.screens.notes.NotesScreen
import com.example.sp2.ui.screens.settings.SettingsScreen
import com.example.sp2.ui.screens.tasks.AddTaskScreen
import com.example.sp2.ui.screens.tasks.TaskDetailScreen
import com.example.sp2.ui.screens.tasks.TaskListDetailScreen
import com.example.sp2.ui.screens.tasks.TaskListViewModel
import com.example.sp2.ui.screens.tasks.TasksScreen
import com.example.sp2.ui.screens.tasks.TaskViewModel

// Controls navigation between the main screens of the app
@Composable
fun AppNavigation() {

    val navController =
        rememberNavController()

    // Shared task ViewModel
    val taskViewModel:
            TaskViewModel = viewModel()

    // Shared task list ViewModel
    val taskListViewModel:
            TaskListViewModel = viewModel()

    // Room tasks
    val tasks by
    taskViewModel.tasks.collectAsState()

    // Room lists
    val taskLists by
    taskListViewModel.taskLists.collectAsState()

    Scaffold(
        bottomBar = {
            AppBottomBar(navController)
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier =
                Modifier.padding(innerPadding)
        ) {

            // Home
            composable(Routes.HOME) {

                HomeScreen(
                    onEditTask = { taskId ->

                        navController.navigate(
                            Routes.taskDetail(taskId)
                        )
                    },
                    taskViewModel =
                        taskViewModel
                )
            }

            // Tasks
            composable(Routes.TASKS) {

                TasksScreen(
                    onAddTask = {

                        navController.navigate(
                            Routes.ADD_TASK
                        )
                    },

                    onEditTask = { taskId ->

                        navController.navigate(
                            Routes.taskDetail(taskId)
                        )
                    },

                    onOpenList = { listId ->

                        navController.navigate(
                            Routes.taskListDetail(
                                listId
                            )
                        )
                    },

                    taskViewModel =
                        taskViewModel,

                    taskListViewModel =
                        taskListViewModel
                )
            }

            // My Stuff
            composable(Routes.MY_STUFF) {

                MyStuffScreen(
                    onAddTask = {

                        navController.navigate(
                            Routes.ADD_TASK
                        )
                    },

                    onAddNote = {

                        navController.navigate(
                            Routes.ADD_NOTE
                        )
                    },

                    onEditTask = { taskId ->

                        navController.navigate(
                            Routes.taskDetail(taskId)
                        )
                    },

                    onOpenNote = { noteId ->

                        navController.navigate(
                            Routes.noteDetail(noteId)
                        )
                    },

                    onOpenList = { listId ->

                        navController.navigate(
                            Routes.taskListDetail(
                                listId
                            )
                        )
                    }
                )
            }

            // New note
            composable(Routes.ADD_NOTE) {

                NotesScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Existing note
            composable(
                route = Routes.NOTE_DETAIL,
                arguments = listOf(
                    navArgument("noteId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val noteId =
                    backStackEntry.arguments
                        ?.getInt("noteId")

                NotesScreen(
                    noteId = noteId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Calendar
            composable(Routes.CALENDAR) {

                CalendarScreen(
                    onEditTask = { taskId ->

                        navController.navigate(
                            Routes.taskDetail(taskId)
                        )
                    },

                    taskViewModel =
                        taskViewModel
                )
            }

            // Settings
            composable(Routes.SETTINGS) {
                SettingsScreen()
            }

            // Add task
            composable(Routes.ADD_TASK) {

                AddTaskScreen(
                    onTaskAdded = {
                        navController.popBackStack()
                    },

                    onBack = {
                        navController.popBackStack()
                    },

                    taskViewModel =
                        taskViewModel,

                    taskListViewModel =
                        taskListViewModel
                )
            }

            // Task detail
            composable(
                route = Routes.TASK_DETAIL,

                arguments = listOf(
                    navArgument("taskId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val taskId =
                    backStackEntry.arguments
                        ?.getInt("taskId")
                        ?: -1

                val task =
                    tasks.find {
                        it.id == taskId
                    }

                if (task != null) {

                    TaskDetailScreen(
                        task = task,

                        taskLists =
                            taskLists,

                        onSave = {
                                updatedTask ->

                            taskViewModel.updateTask(
                                updatedTask
                            )

                            navController
                                .popBackStack()
                        },

                        onDelete = {

                            taskViewModel.deleteTask(
                                task
                            )

                            navController
                                .popBackStack()
                        },

                        onBack = {
                            navController
                                .popBackStack()
                        }
                    )
                }
            }

            // Task list detail
            composable(
                route =
                    Routes.TASK_LIST_DETAIL,

                arguments = listOf(
                    navArgument("listId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val listId =
                    backStackEntry.arguments
                        ?.getInt("listId")
                        ?: -1

                val taskList =
                    taskLists.find {
                        it.id == listId
                    }

                if (taskList != null) {

                    TaskListDetailScreen(
                        taskList = taskList,

                        onBack = {
                            navController
                                .popBackStack()
                        },

                        onEditTask = {
                                taskId ->

                            navController.navigate(
                                Routes.taskDetail(
                                    taskId
                                )
                            )
                        },

                        taskViewModel =
                            taskViewModel
                    )
                }
            }
        }
    }
}