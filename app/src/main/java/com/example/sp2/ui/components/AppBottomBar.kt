package com.example.sp2.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sp2.R
import com.example.sp2.navigation.Routes
import com.example.sp2.ui.screens.home.HabitViewModel
import com.example.sp2.ui.screens.reminders.RemindersViewModel

// Represents an item in the bottom navigation bar
data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun AppBottomBar(
    navController: NavHostController,
    habitViewModel: HabitViewModel,
    remindersViewModel: RemindersViewModel
) {
    // Controls whether the create dialog is visible
    var showCreateDialog by remember {
        mutableStateOf(false)
    }

    // Controls the "new habit" dialog, shown directly from here
    // since creating a habit isn't a navigable screen
    var showCreateHabitDialog by remember {
        mutableStateOf(false)
    }

    // Controls the "new alarm" dialog, same reasoning
    var showCreateAlarmDialog by remember {
        mutableStateOf(false)
    }

    // Defines the screens that will appear in the bottom navigation bar
    val items = listOf(
        BottomNavItem(
            title = stringResource(R.string.nav_home),
            route = Routes.HOME,
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            title = stringResource(R.string.nav_my_stuff),
            route = Routes.MY_STUFF,
            icon = Icons.Default.CheckCircle
        ),
        BottomNavItem(
            title = "",
            route = "add",
            icon = Icons.Default.Add
        ),
        BottomNavItem(
            title = stringResource(R.string.nav_calendar),
            route = Routes.CALENDAR,
            icon = Icons.Default.DateRange
        ),
        BottomNavItem(
            title = stringResource(R.string.nav_settings),
            route = Routes.SETTINGS,
            icon = Icons.Default.Settings
        )
    )

    // Gets the current navigation destination
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Creates the bottom navigation bar
    NavigationBar {
        items.forEach { item ->

            NavigationBarItem(
                selected = item.route == currentRoute,
                onClick = {
                    // The center button opens the create dialog
                    if (item.route == "add") {
                        showCreateDialog = true
                    } else {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    if (item.title.isNotEmpty()) {
                        Text(item.title)
                    }
                }
            )
        }
    }

    // Create dialog
    if (showCreateDialog) {
        CreateActionDialog(
            onDismiss = {
                showCreateDialog = false
            },
            onCreateNote = {
                showCreateDialog = false
                navController.navigate(Routes.ADD_NOTE)
            },
            onCreateTask = {
                showCreateDialog = false
                navController.navigate(Routes.ADD_TASK)
            },
            onCreateHabit = {
                showCreateDialog = false
                showCreateHabitDialog = true
            },
            onCreateAlarm = {
                showCreateDialog = false
                showCreateAlarmDialog = true
            }
        )
    }

    // Create alarm dialog
    if (showCreateAlarmDialog) {
        CreateAlarmDialog(
            onDismiss = {
                showCreateAlarmDialog = false
            },
            onCreate = { time, label, days, soundUri, snoozeMinutes ->

                remindersViewModel.addAlarm(
                    time = time,
                    label = label,
                    days = days,
                    soundUri = soundUri,
                    snoozeMinutes = snoozeMinutes
                )

                showCreateAlarmDialog = false
            }
        )
    }

    // Create habit dialog
    if (showCreateHabitDialog) {
        CreateHabitDialog(
            onDismiss = {
                showCreateHabitDialog = false
            },
            onCreate = { name, type, frequency, targetCount, durationType, totalPeriods ->

                habitViewModel.addHabit(
                    name = name,
                    type = type,
                    frequency = frequency,
                    targetCount = targetCount,
                    durationType = durationType,
                    totalPeriods = totalPeriods
                )

                showCreateHabitDialog = false
            }
        )
    }
}