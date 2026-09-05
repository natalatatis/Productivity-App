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
    // Controls whether the create dialog is visible
    var showCreateDialog by remember {
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
            }
        )
    }
}