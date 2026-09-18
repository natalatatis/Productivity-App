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
import com.example.sp2.ui.screens.home.HabitViewModel
import com.example.sp2.ui.screens.reminders.RemindersViewModel
import com.example.sp2.ui.screens.home.HomeScreen
import com.example.sp2.ui.screens.mystuff.MyStuffScreen
import com.example.sp2.ui.screens.notes.NoteFolderDetailScreen
import com.example.sp2.ui.screens.notes.NoteFoldersScreen
import com.example.sp2.ui.screens.tasks.TaskFoldersScreen
import com.example.sp2.ui.screens.notes.NoteListViewModel
import com.example.sp2.ui.screens.notes.NotesScreen
import com.example.sp2.ui.screens.reminders.RemindersScreen
import com.example.sp2.ui.screens.settings.SettingsScreen
import com.example.sp2.ui.screens.tasks.AddTaskScreen
import com.example.sp2.ui.screens.tasks.TaskDetailScreen
import com.example.sp2.ui.screens.tasks.TaskListDetailScreen
import com.example.sp2.ui.screens.tasks.TaskListViewModel
import com.example.sp2.ui.screens.tasks.TasksScreen
import com.example.sp2.ui.screens.tasks.TaskViewModel

// Controls navigation between the main screens of the app
@Composable
fun AppNavigation(
    authViewModel: com.example.sp2.ui.screens.auth.AuthViewModel
) {

    val navController =
        rememberNavController()

    // Shared task ViewModel
    val taskViewModel:
            TaskViewModel = viewModel()

    // Shared task list ViewModel
    val taskListViewModel:
            TaskListViewModel = viewModel()

    // Shared habit ViewModel — needed both in Home and in the
    // global "+" button, since creating a habit is a dialog,
    // not a separate screen
    val habitViewModel:
            HabitViewModel = viewModel()

    // Shared note-folder ViewModel
    val noteListViewModel:
            NoteListViewModel = viewModel()

    // Shared reminders ViewModel — needed for creating alarms
    // from the global "+" button
    val remindersViewModel:
            RemindersViewModel = viewModel()

    val noteLists by
    noteListViewModel.noteLists.collectAsState()

    // Room tasks
    val tasks by
    taskViewModel.tasks.collectAsState()

    // Room lists
    val taskLists by
    taskListViewModel.taskLists.collectAsState()

    Scaffold(
        bottomBar = {
            AppBottomBar(
                navController = navController,
                habitViewModel = habitViewModel,
                remindersViewModel = remindersViewModel
            )
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
                    onAddTask = {
                        navController.navigate(Routes.ADD_TASK)
                    },
                    onOpenReminders = {
                        navController.navigate(Routes.REMINDERS)
                    },
                    taskViewModel =
                        taskViewModel,
                    habitViewModel =
                        habitViewModel
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

                    onOpenFolders = { navController.navigate(Routes.TASK_FOLDERS) },

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
                            Routes.addNote()
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

                    onOpenTaskFolders = {

                        navController.navigate(
                            Routes.TASK_FOLDERS
                        )
                    },

                    onOpenNoteFolders = {

                        navController.navigate(
                            Routes.NOTE_FOLDERS
                        )
                    }
                )
            }

            // Task folders list
            composable(Routes.TASK_FOLDERS) {

                TaskFoldersScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onOpenFolder = { listId ->
                        navController.navigate(
                            Routes.taskListDetail(listId)
                        )
                    },
                    taskListViewModel = taskListViewModel
                )
            }

            // Note folders list
            composable(Routes.NOTE_FOLDERS) {

                NoteFoldersScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onOpenFolder = { listId ->
                        navController.navigate(
                            Routes.noteFolderDetail(listId)
                        )
                    },
                    noteListViewModel = noteListViewModel
                )
            }

            // Notes inside one folder
            composable(
                route = Routes.NOTE_FOLDER_DETAIL,
                arguments = listOf(
                    navArgument("listId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val listId =
                    backStackEntry.arguments?.getInt("listId") ?: -1

                val noteList = noteLists.find { it.id == listId }

                if (noteList != null) {

                    NoteFolderDetailScreen(
                        noteList = noteList,
                        onBack = {
                            navController.popBackStack()
                        },
                        onOpenNote = { noteId ->
                            navController.navigate(
                                Routes.noteDetail(noteId)
                            )
                        },
                        onAddNote = {
                            navController.navigate(
                                Routes.addNote(listId)
                            )
                        }
                    )
                }
            }

            // New note
            composable(
                route = Routes.ADD_NOTE,
                arguments = listOf(
                    navArgument("listId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->

                val listId = backStackEntry.arguments?.getInt("listId")
                    ?.takeIf { it != -1 }

                NotesScreen(
                    initialListId = listId,
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
                SettingsScreen(authViewModel = authViewModel)
            }

            // Reminders
            composable(Routes.REMINDERS) {
                RemindersScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
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

                        onSave = { updatedTask ->

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

                        onEditTask = { taskId ->

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