package com.example.sp2.ui.components

import androidx.compose.material.icons.Icons
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

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun AppBottomBar(
    navController: NavHostController
) {

    val items = listOf(
        BottomNavItem(
            title = "Home",
            route = Routes.HOME,
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            title = "Tasks",
            route = Routes.TASKS,
            icon = Icons.Default.CheckCircle
        ),
        BottomNavItem(
            title = "Calendar",
            route = Routes.CALENDAR,
            icon = Icons.Default.DateRange
        ),
        BottomNavItem(
            title = "Settings",
            route = Routes.SETTINGS,
            icon = Icons.Default.Settings
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {

        items.forEach { item ->

            NavigationBarItem(
                selected = currentRoute == item.route,

                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                },

                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },

                label = {
                    Text(item.title)
                }
            )
        }
    }
}