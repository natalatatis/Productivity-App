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

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

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

            composable(Routes.HOME) {
                HomeScreen()
            }

            composable(Routes.TASKS) {
                TasksScreen()
            }

            composable(Routes.CALENDAR) {
                CalendarScreen()
            }

            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
        }
    }
}