package com.example.sp2.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sp2.data.TaskManager
import com.example.sp2.ui.components.AppBottomBar
import com.example.sp2.ui.screens.calendar.CalendarScreen
import com.example.sp2.ui.screens.home.HomeScreen
import com.example.sp2.ui.screens.settings.SettingsScreen
import com.example.sp2.ui.screens.tasks.TasksScreen
import com.example.sp2.ui.screens.tasks.AddTaskScreen
import com.example.sp2.ui.screens.tasks.TaskDetailScreen
import com.example.sp2.ui.screens.notes.NotesScreen
import com.example.sp2.ui.screens.notes.NotesViewModel
import com.example.sp2.ui.screens.mystuff.MyStuffScreen

// Controls the navigation between the main screens of the app
@Composable
fun AppNavigation() {

    // Creates and remembers the navigation controller
    val navController = rememberNavController()

    // Basic layout of the app
    // Navbar at the bottom is displayed on every main screen
    Scaffold(
        bottomBar = {
            AppBottomBar(navController)
        }
    ) { innerPadding ->

        // Screens that can be accesses through navigation
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {

            // Home
            composable(Routes.HOME) {
                HomeScreen(
                    onEditTask = { taskId ->
                        navController.navigate(Routes.taskDetail(taskId))
                    }
                )
            }

            // Tasks
            composable(Routes.TASKS) {
                TasksScreen(
                    onAddTask = {
                        navController.navigate(Routes.ADD_TASK)
                    },
                    onEditTask = { taskId ->
                        navController.navigate(Routes.taskDetail(taskId))
                    }
                )
            }

            // My stuff
            composable(Routes.MY_STUFF) {
                MyStuffScreen(
                    onAddTask = {
                        navController.navigate(Routes.ADD_TASK)
                    },
                    onAddNote = {
                        navController.navigate(Routes.ADD_NOTE)
                    }
                )
            }

            // Notes
            composable(Routes.ADD_NOTE) {
                NotesScreen(
                    onNoteSaved = {
                        navController.popBackStack()
                    }
                )
            }

            // Calendar
            composable(Routes.CALENDAR) {
                CalendarScreen(
                    onEditTask = { taskId ->
                        navController.navigate(Routes.taskDetail(taskId))
                    }
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
                    }
                )
            }

            // Task Detail (edit an existing task)
            composable(
                route = Routes.TASK_DETAIL,
                arguments = listOf(
                    navArgument("taskId") { type = NavType.IntType }
                )
            ) { backStackEntry ->

                val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
                val task = TaskManager.tasks.find { it.id == taskId }

                // Only shows the screen if the task still exists
                if (task != null) {
                    TaskDetailScreen(
                        task = task,
                        onSave = { updatedTask ->
                            TaskManager.updateTask(updatedTask)
                            navController.popBackStack()
                        },
                        onDelete = {
                            TaskManager.deleteTask(task)
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