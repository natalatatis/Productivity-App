package com.example.sp2.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sp2.ui.components.AppBottomBar
import com.example.sp2.ui.screens.calendar.CalendarScreen
import com.example.sp2.ui.screens.home.HomeScreen
import com.example.sp2.ui.screens.settings.SettingsScreen
import com.example.sp2.ui.screens.tasks.TasksScreen
import com.example.sp2.ui.screens.tasks.AddTaskScreen

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
                HomeScreen()
            }

            // Tasks
            composable(Routes.TASKS) {
                TasksScreen(
                    onAddTask = {
                        navController.navigate(Routes.ADD_TASK)
                    }
                )
            }

            // Calendar
            composable(Routes.CALENDAR) {
                CalendarScreen()
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
                    }
                )
            }
        }
    }
}