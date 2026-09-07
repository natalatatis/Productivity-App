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
import com.example.sp2.ui.screens.tasks.TasksScreen
import com.example.sp2.ui.screens.tasks.TaskViewModel

// Controls the navigation between the main screens of the app
@Composable
fun AppNavigation() {

    // Creates and remembers the navigation controller
    val navController = rememberNavController()

    // Shared TaskViewModel
    val taskViewModel: TaskViewModel = viewModel()

    // Observes tasks stored in Room
    val tasks by taskViewModel.tasks.collectAsState()

    // Basic layout of the app
    Scaffold(
        bottomBar = {
            AppBottomBar(navController)
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {

            // Home
            composable(Routes.HOME) {
                HomeScreen(
                    onEditTask = { taskId ->
                        navController.navigate(
                            Routes.taskDetail(taskId)
                        )
                    },
                    taskViewModel = taskViewModel
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
                    taskViewModel = taskViewModel
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
                    }
                )
            }

            // Creates a new note
            // Creates a new note
            composable(Routes.ADD_NOTE) {
                NotesScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Opens an existing note
            composable(
                route = Routes.NOTE_DETAIL,
                arguments = listOf(
                    navArgument("noteId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val noteId =
                    backStackEntry.arguments?.getInt("noteId")

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
                    taskViewModel = taskViewModel
                )
            }

            // Settings
            composable(Routes.SETTINGS) {
                SettingsScreen()
            }

            // Add Task
            composable(Routes.ADD_TASK) {
                AddTaskScreen(
                    onTaskAdded = {
                        navController.popBackStack()
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    taskViewModel = taskViewModel
                )
            }

            // Task Detail
            composable(
                route = Routes.TASK_DETAIL,
                arguments = listOf(
                    navArgument("taskId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val taskId =
                    backStackEntry.arguments?.getInt("taskId") ?: -1

                // Finds the task in Room data
                val task = tasks.find {
                    it.id == taskId
                }

                if (task != null) {

                    TaskDetailScreen(
                        task = task,

                        // Updates task in Room
                        onSave = { updatedTask ->
                            taskViewModel.updateTask(
                                updatedTask
                            )

                            navController.popBackStack()
                        },

                        // Deletes task from Room
                        onDelete = {
                            taskViewModel.deleteTask(
                                task
                            )

                            navController.popBackStack()
                        },

                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}