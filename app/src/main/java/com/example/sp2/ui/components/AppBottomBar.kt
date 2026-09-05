package com.example.sp2.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sp2.navigation.Routes
import androidx.compose.ui.res.stringResource
import com.example.sp2.R

// Represents an item in the bottom navigation bar
data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun AppBottomBar(
    navController: NavHostController
) {

    // Defines the screens that will appear in the bottom navigation bar
    val items = listOf(
        BottomNavItem(
            title = stringResource(R.string.nav_home),
            route = Routes.HOME,
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            title = stringResource(R.string.nav_tasks),
            route = Routes.TASKS,
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
                selected = currentRoute == item.route,

                onClick = {
                    if (item.route == "add") {
                        navController.navigate(Routes.ADD_NOTE)
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
}