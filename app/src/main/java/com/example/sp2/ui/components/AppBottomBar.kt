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

// Represents an item in the bottom navigation bar
// Each item has a name, route and icon
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

    // Gets the current navigation destination
    // This allows the app to know which item is currently selected
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Creates the bottom navigation bar
    NavigationBar {

        // Creates one navigation item for each screen
        items.forEach { item ->

            NavigationBarItem(

                // Highlights the item corresponding to the current screen
                selected = currentRoute == item.route,

                // Navigates to the selected screen when the item is clicked
                onClick = {
                    navController.navigate(item.route) {

                        // Prevents creating duplicate instances of the same screen
                        launchSingleTop = true
                    }
                },

                // Displays the icon for the navigation item
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },

                // Displays the name below the icon
                label = {
                    Text(item.title)
                }
            )
        }
    }
}